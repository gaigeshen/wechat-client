package me.gaigeshen.wechat.client.core.accesstoken;

import me.gaigeshen.wechat.client.core.Constants;
import me.gaigeshen.wechat.client.core.http.WebClient;
import me.gaigeshen.wechat.client.core.http.WebClientException;
import me.gaigeshen.wechat.client.core.request.DefaultResponse;
import me.gaigeshen.wechat.client.core.request.RequestResultException;
import me.gaigeshen.wechat.client.core.request.ResponseParseException;
import me.gaigeshen.wechat.client.core.request.ResponseParser;
import me.gaigeshen.wechat.client.core.util.Asserts;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * 采用线程池的实现来用于更新访问令牌，访问令牌被安排为单独的线程任务被调度，前提是调用了启动方法
 *
 * @author gaigeshen
 */
public class AccessTokenManagerImpl implements AccessTokenManager {

  private static final Logger logger = LoggerFactory.getLogger(AccessTokenManagerImpl.class);

  private static final int DEFAULT_THREAD_POOL_SIZE = 1;

  private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(DEFAULT_THREAD_POOL_SIZE);

  private final WebClient webClient = WebClient.builder().build();

  private final ResponseParser<AccessToken> accessTokenResponseParser = new AccessTokenResponseParserImpl();

  private final AppConfigStore appConfigStore;

  private final AccessTokenStore accessTokenStore;

  public AccessTokenManagerImpl(AppConfigStore appConfigStore, AccessTokenStore accessTokenStore) {
    this.appConfigStore = new AppConfigStoreProxy(Asserts.notNull(appConfigStore, "appConfigStore"));
    this.accessTokenStore = new AccessTokenStoreCacheWrapper(Asserts.notNull(accessTokenStore, "accessTokenStore"));
  }

  @Override
  public AppConfigStore getAppConfigStore() {
    return appConfigStore;
  }

  @Override
  public AccessTokenStore getAccessTokenStore() {
    return accessTokenStore;
  }

  @Override
  public AccessToken findAccessToken(String appid) throws AccessTokenManagerException {
    try {
      return accessTokenStore.findByAppid(appid);
    } catch (AccessTokenStoreException e) {
      throw new AccessTokenManagerException("Could not find access token because store exception:: " + appid, e);
    }
  }

  @Override
  public void saveAccessToken(AccessToken accessToken) throws AccessTokenManagerException {
    try {
      if (accessTokenStore.saveOrUpdate(accessToken)) {
        new AccessTokenUpdateTask(accessToken).start();
      }
    } catch (AccessTokenStoreException e) {
      throw new AccessTokenManagerException("Could not save access token because store exception:: " + accessToken.getAppid(), e);
    }
  }

  @Override
  public void deleteAccessToken(String appid) throws AccessTokenManagerException {
    try {
      accessTokenStore.deleteByAppid(appid);
    } catch (AccessTokenStoreException e) {
      throw new AccessTokenManagerException("Could not delete access token because store exception:: " + appid, e);
    }
  }

  @Override
  public void startup() throws AccessTokenManagerException {
    try {
      for (AccessToken accessToken : accessTokenStore.findAll()) {
        new AccessTokenUpdateTask(accessToken).start();
      }
    } catch (AccessTokenStoreException e) {
      throw new AccessTokenManagerException("Could not startup access token manager because cannot find all access tokens from store", e);
    }
  }

  @Override
  public void shutdown() throws AccessTokenManagerException {
    executorService.shutdownNow();
    try {
      executorService.awaitTermination(10, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      throw new AccessTokenManagerException("Current thread interrupted while shutting down access token manager", e);
    } finally {
      try {
        webClient.close();
      } catch (IOException ignored) {
      }
    }
  }

  /**
   * @throws AppConfigStoreException 应用配置存储器异常
   * @throws NoSuchAppConfigException 没有找到该应用配置
   */
  private AppConfig findExistsAppConfig(String appid) throws AppConfigStoreException {
    return appConfigStore.findRequiredByAppid(appid);
  }

  /**
   * @throws AccessTokenStoreException 访问令牌存储器异常
   * @throws NoSuchAccessTokenException 没有找到该访问令牌
   */
  private void checkExistsAccessToken(String appid) throws AccessTokenStoreException {
    accessTokenStore.findRequiredByAppid(appid);
  }

  /**
   * @throws WebClientException 网络异常
   * @throws ResponseParseException 结果转换异常
   * @throws RequestResultException 请求执行结果异常，即业务异常
   */
  private AccessToken getRemoteAccessToken(AppConfig appConfig) throws WebClientException, ResponseParseException, RequestResultException {
    HttpGet req = new HttpGet(Constants.getAccessTokenUrl(appConfig.getAppid(), appConfig.getSecret()));
    DefaultResponse response = DefaultResponse.create(webClient.execute(req));
    return accessTokenResponseParser.parse(appConfig.getAppid(), response);
  }

  /**
   * 获取远程的访问令牌，通过应用编号来获取，该应用编号对应的应用配置当前必须已存在，且该应用编号对应的访问令牌也必须已存在，即如果在执行此方法时，由于某种原因当前的访问令牌或者应用配置已被删除的情况下，会抛出对应的异常
   *
   * @param appid 应用编号
   * @return 访问令牌
   * @throws AppConfigStoreException 可能抛出应用配置存储器异常，也有可能找不到指定的应用配置时报异常
   * @throws AccessTokenStoreException 可能抛出访问令牌存储器异常，也有可能找不到指定的访问令牌时报异常
   * @throws WebClientException 如果抛出此异常，建议重试
   * @throws ResponseParseException 转换原始响应结果的时候发生异常
   * @throws RequestResultException 请求执行结果异常，比如原始响应的结果为失败的结果，或者请求执行结果内容不符合预期的格式
   */
  private AccessToken getRemoteAccessToken(String appid) throws AppConfigStoreException, AccessTokenStoreException, WebClientException, ResponseParseException, RequestResultException {
    // 查询已经存在的访问令牌，如果不出在则抛出异常
    checkExistsAccessToken(appid);
    // 查询已经存在的应用配置，如果不存在则抛出异常，如果存在则继续获取远程的访问令牌
    // 此方法的调用可能会抛出网络或者结果转换异常，也有可能为结果不是预期的格式导致的请求执行结果异常
    return getRemoteAccessToken(findExistsAppConfig(appid));
  }

  /**
   * 访问令牌更新任务
   *
   * @author gaigeshen
   */
  private class AccessTokenUpdateTask implements Runnable {

    private final AccessToken currentAccessToken;

    public AccessTokenUpdateTask(AccessToken currentAccessToken) {
      this.currentAccessToken = currentAccessToken;
    }

    public void start(long delaySeconds) {
      try {
        executorService.schedule(this, delaySeconds, TimeUnit.SECONDS);
      } catch (Exception e) {
        // This cannot be happening
        logger.warn("Could not schedule access token update task:: " + currentAccessToken.getAppid(), e);
      }
    }
    public void start() {
      start(calcActionDelaySeconds(currentAccessToken));
    }
    public void startWithNewAccessToken(AccessToken accessToken) {
      new AccessTokenUpdateTask(accessToken).start();
    }

    private long calcActionDelaySeconds(AccessToken accessToken) {
      return (accessToken.getExpiresTimestamp() - 1800) - System.currentTimeMillis() / 1000;
    }

    private void printCurrentAccessTokenBeforeRun() {
      logger.info("Run access token update task:: " + currentAccessToken.getAppid());
    }

    @Override
    public void run() {
      printCurrentAccessTokenBeforeRun();
      AccessToken remoteAccessToken;
      try {
        remoteAccessToken = getRemoteAccessToken(currentAccessToken.getAppid());
      } catch (Exception e) {
        if (e instanceof NoSuchAccessTokenException || e instanceof NoSuchAppConfigException) {
          // 该应用不存在当前的访问令牌或者没有对应的应用配置，放弃对该应用的访问令牌的更新操作
          logger.warn("Could not get remote access token because no current access token or no app config:: " + currentAccessToken.getAppid(), e);
        } else {
          // 准备再次尝试，因为此时可能是网络异常，或者相关的存储器异常
          logger.warn("Could not get remote access token, try again 10 seconds later:: " + currentAccessToken.getAppid(), e);
          start(10);
        }
        return;
      }
      // 获取远程的访问成功，调度下次的访问令牌更新任务
      startWithNewAccessToken(remoteAccessToken);
      // 保存或者更新访问令牌
      try {
        accessTokenStore.saveOrUpdate(remoteAccessToken);
      } catch (Exception e) {
        // 执行更新访问令牌的时候发生异常，需要重试吗？
        // 此时的异常可能是本地存储状态发生异常，往后肯定会修复正常，后续更新操作肯定会同步到本地存储
        // 建议访问令牌存储器对这类情况做相应的处理
        logger.warn("Could not update access token to store:: " + currentAccessToken.getAppid(), e);
      }
    }
  }

  /**
   * 访问令牌存储器缓存包装，对缓存中的操作不会抛出异常，所有的异常均来自原始存储器
   *
   * @author gaigeshen
   */
  private class AccessTokenStoreCacheWrapper implements AccessTokenStore {

    private final AccessTokenStore internalStore = AccessTokenStoreImpl.create();

    private final AccessTokenStore originStore;

    private AccessTokenStoreCacheWrapper(AccessTokenStore originStore) {
      this.originStore = originStore;
    }

    // 先保存到缓存中，再保存到原始存储器
    @Override
    public boolean saveOrUpdate(AccessToken accessToken) throws AccessTokenStoreException {
      internalStore.saveOrUpdate(accessToken);
      return originStore.saveOrUpdate(accessToken);
    }

    // 先删除缓存中的，再删除原始的
    @Override
    public void deleteByAppid(String appid) throws AccessTokenStoreException {
      internalStore.deleteByAppid(appid);
      originStore.deleteByAppid(appid);
    }

    // 直接查询缓存中的，缓存中不存在再去查询原始的
    @Override
    public AccessToken findByAppid(String shopId) throws AccessTokenStoreException {
      AccessToken accessToken = internalStore.findByAppid(shopId);
      if (Objects.nonNull(accessToken)) {
        return accessToken;
      }
      AccessToken accessTokenFromOrigin = originStore.findByAppid(shopId);
      if (Objects.nonNull(accessTokenFromOrigin)) {
        internalStore.saveOrUpdate(accessTokenFromOrigin);
      }
      return accessTokenFromOrigin;
    }

    // 直接查询缓存中的，缓存中不存在再去查询原始的，没有考虑缓存和原始数据非空不同集合的情况
    @Override
    public Collection<AccessToken> findAll() throws AccessTokenStoreException {
      Collection<AccessToken> accessTokens = internalStore.findAll();
      if (!accessTokens.isEmpty()) {
        return accessTokens;
      }
      Collection<AccessToken> accessTokensFromOrigin = originStore.findAll();
      if (!accessTokensFromOrigin.isEmpty()) {
        for (AccessToken accessTokenFromOrigin : accessTokensFromOrigin) {
          internalStore.saveOrUpdate(accessTokenFromOrigin);
        }
      }
      return accessTokensFromOrigin;
    }
  }

  /**
   * 应用配置存储器代理
   *
   * @author gaigeshen
   */
  private class AppConfigStoreProxy implements AppConfigStore {

    private final AppConfigStore originStore;

    private AppConfigStoreProxy(AppConfigStore originStore) {
      this.originStore = originStore;
    }

    @Override
    public boolean saveOrUpdate(AppConfig appConfig) throws AppConfigStoreException {
      // 不管该应用配置是否已经存在，此时获取远程的访问令牌先
      AccessToken remoteAccessToken;
      try {
        remoteAccessToken = getRemoteAccessToken(appConfig);
      } catch (Exception e) {
        throw new AppConfigStoreException("Could not get remote access token:: " + appConfig.getAppid(), e);
      }
      try {
        saveAccessToken(remoteAccessToken); // 如果是新应用的访问令牌，则会开启新的更新访问令牌的任务
      } catch (AccessTokenManagerException e) {
        throw new AppConfigStoreException("Could not save access token:: " + appConfig.getAppid(), e);
      }
      return originStore.saveOrUpdate(appConfig);
    }

    @Override
    public void deleteByAppid(String appid) throws AppConfigStoreException {
      originStore.deleteByAppid(appid);
    }

    @Override
    public AppConfig findByAppid(String appid) throws AppConfigStoreException {
      return originStore.findByAppid(appid);
    }

    @Override
    public Collection<AppConfig> findAll() throws AppConfigStoreException {
      return originStore.findAll();
    }
  }
}
