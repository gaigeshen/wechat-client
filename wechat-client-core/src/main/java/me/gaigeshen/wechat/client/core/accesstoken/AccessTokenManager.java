package me.gaigeshen.wechat.client.core.accesstoken;

/**
 * 访问令牌管理器，内部需要访问令牌存储器用于访问令牌的存储和获取，同时维护访问令牌的更新
 *
 * @author gaigeshen
 */
public interface AccessTokenManager {
  /**
   * 返回内部使用的应用配置存储器
   *
   * @return 应用配置存储器
   */
  AppConfigStore getAppConfigStore();

  /**
   * 返回内部使用的访问令牌存储器
   *
   * @return 访问令牌存储器
   */
  AccessTokenStore getAccessTokenStore();

  /**
   * 查询访问令牌
   *
   * @param appid 应用编号
   * @return 访问令牌
   * @throws AccessTokenManagerException 访问令牌管理器异常
   */
  AccessToken findAccessToken(String appid) throws AccessTokenManagerException;

  /**
   * 保存访问令牌
   *
   * @param accessToken 访问令牌
   * @throws AccessTokenManagerException 访问令牌管理器异常
   */
  void saveAccessToken(AccessToken accessToken) throws AccessTokenManagerException;

  /**
   * 删除访问令牌
   *
   * @param appid 应用编号
   * @throws AccessTokenManagerException 访问令牌管理器异常
   */
  void deleteAccessToken(String appid) throws AccessTokenManagerException;

  /**
   * 启动此访问令牌管理器
   *
   * @throws AccessTokenManagerException 访问令牌管理器异常
   */
  void startup() throws AccessTokenManagerException;

  /**
   * 关闭此访问令牌管理器
   *
   * @throws AccessTokenManagerException 访问令牌管理器异常
   */
  void shutdown() throws AccessTokenManagerException;

}
