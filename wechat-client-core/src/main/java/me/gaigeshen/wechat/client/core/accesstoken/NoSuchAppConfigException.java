package me.gaigeshen.wechat.client.core.accesstoken;

/**
 * @author gaigeshen
 */
public class NoSuchAppConfigException extends AppConfigStoreException {
  public NoSuchAppConfigException(String message) {
    super(message);
  }
  public NoSuchAppConfigException(String message, Throwable cause) {
    super(message, cause);
  }
}
