package me.gaigeshen.wechat.client.core.accesstoken;

/**
 * 访问令牌存储器异常
 *
 * @author gaigeshen
 */
public class AccessTokenStoreException extends Exception {
  public AccessTokenStoreException(String message) {
    super(message);
  }

  public AccessTokenStoreException(String message, Throwable cause) {
    super(message, cause);
  }
}
