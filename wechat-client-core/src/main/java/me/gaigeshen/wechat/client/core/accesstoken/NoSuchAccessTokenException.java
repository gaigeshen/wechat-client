package me.gaigeshen.wechat.client.core.accesstoken;

/**
 * 当存储器中不存在访问令牌时的异常
 *
 * @author gaigeshen
 */
public class NoSuchAccessTokenException extends AccessTokenStoreException {
  public NoSuchAccessTokenException(String message) {
    super(message);
  }
  public NoSuchAccessTokenException(String message, Throwable cause) {
    super(message, cause);
  }
}
