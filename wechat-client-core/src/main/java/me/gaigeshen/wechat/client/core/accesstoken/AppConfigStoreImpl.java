package me.gaigeshen.wechat.client.core.accesstoken;

import me.gaigeshen.wechat.client.core.util.Asserts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用配置存储器默认的实现，采用简单的哈希存储
 *
 * @author gaigeshen
 */
public class AppConfigStoreImpl implements AppConfigStore {

  private final Map<String, AppConfig> internalStore = new ConcurrentHashMap<>();

  private AppConfigStoreImpl() { }

  public static AppConfigStoreImpl create() {
    return new AppConfigStoreImpl();
  }

  @Override
  public boolean saveOrUpdate(AppConfig appConfig) {
    Asserts.notNull(appConfig, "appConfig");
    return Objects.isNull(internalStore.put(appConfig.getAppid(), appConfig));
  }

  @Override
  public void deleteByAppid(String appid) {
    internalStore.remove(Asserts.notBlank(appid, "appid"));
  }

  @Override
  public AppConfig findByAppid(String appid) {
    return internalStore.get(Asserts.notBlank(appid, "appid"));
  }

  @Override
  public Collection<AppConfig> findAll() {
    return new ArrayList<>(internalStore.values());
  }
}
