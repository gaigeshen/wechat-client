package me.gaigeshen.wechat.client.core.accesstoken;

import java.util.Collection;
import java.util.Objects;

/**
 * @author gaigeshen
 */
public interface AppConfigStore {

  boolean saveOrUpdate(AppConfig appConfig) throws AppConfigStoreException;

  void deleteByAppid(String appid) throws AppConfigStoreException;

  AppConfig findByAppid(String appid) throws AppConfigStoreException;

  default AppConfig findRequiredByAppid(String appid) throws AppConfigStoreException {
    AppConfig appConfig = findByAppid(appid);
    if (Objects.isNull(appConfig)) {
      throw new NoSuchAppConfigException("No app config:: " + appid);
    }
    return appConfig;
  }

  Collection<AppConfig> findAll() throws AppConfigStoreException;

}
