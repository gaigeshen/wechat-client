package me.gaigeshen.wechat.client.core.accesstoken;

import java.util.Collection;
import java.util.Objects;

/**
 * 访问令牌存储器
 *
 * @author gaigeshen
 */
public interface AccessTokenStore {
  /**
   * 保存或者更新访问令牌，通过返回值确定本次操作的是否为新应用的访问令牌
   *
   * @param accessToken 访问令牌
   * @return 是否为新应用的访问令牌
   * @throws AccessTokenStoreException 访问令牌存储器异常
   */
  boolean saveOrUpdate(AccessToken accessToken) throws AccessTokenStoreException;

  /**
   * 删除访问令牌
   *
   * @param appid 应用编号
   * @throws AccessTokenStoreException 访问令牌存储器异常
   */
  void deleteByAppid(String appid) throws AccessTokenStoreException;

  /**
   * 查询访问令牌
   *
   * @param appid 应用编号
   * @return 访问令牌
   * @throws AccessTokenStoreException 访问令牌存储器异常
   */
  AccessToken findByAppid(String appid) throws AccessTokenStoreException;

  /**
   * 查询访问令牌，该访问令牌必须存在，否则抛出异常
   *
   * @param appid 应用编号
   * @return 访问令牌
   * @throws AccessTokenStoreException 访问令牌存储器异常
   * @throws NoSuchAccessTokenException 当访问令牌不存在时
   */
  default AccessToken findRequiredByAppid(String appid) throws AccessTokenStoreException {
    AccessToken accessToken = findByAppid(appid);
    if (Objects.isNull(accessToken)) {
      throw new NoSuchAccessTokenException("No access token:: " + appid);
    }
    return accessToken;
  }

  /**
   * 查询所有的访问令牌
   *
   * @return 所有的访问令牌
   * @throws AccessTokenStoreException 访问令牌存储器异常
   */
  Collection<AccessToken> findAll() throws AccessTokenStoreException;

}
