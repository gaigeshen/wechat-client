package me.gaigeshen.wechat.client.core.accesstoken;

import me.gaigeshen.wechat.client.core.util.Asserts;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 访问令牌存储器默认的实现，采用简单的哈希存储
 *
 * @author gaigeshen
 */
public class AccessTokenStoreImpl implements AccessTokenStore {

  private final Map<String, AccessToken> internalStore = new ConcurrentHashMap<>();

  private AccessTokenStoreImpl() { }

  public static AccessTokenStoreImpl create() {
    return new AccessTokenStoreImpl();
  }

  @Override
  public boolean saveOrUpdate(AccessToken accessToken) {
    Asserts.notNull(accessToken, "accessToken");
    return Objects.isNull(internalStore.put(accessToken.getAppid(), accessToken));
  }

  @Override
  public void deleteByAppid(String appid) {
    internalStore.remove(Asserts.notBlank(appid, "appid"));
  }

  @Override
  public AccessToken findByAppid(String appid) {
    return internalStore.get(Asserts.notBlank(appid, "appid"));
  }

  @Override
  public Collection<AccessToken> findAll() {
    return new ArrayList<>(internalStore.values());
  }
}
