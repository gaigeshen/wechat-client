package me.gaigeshen.wechat.client.core.accesstoken;

/**
 * @author gaigeshen
 */
public class AccessTokenManagerException extends Exception {
  public AccessTokenManagerException(String message) {
    super(message);
  }

  public AccessTokenManagerException(String message, Throwable cause) {
    super(message, cause);
  }
}
