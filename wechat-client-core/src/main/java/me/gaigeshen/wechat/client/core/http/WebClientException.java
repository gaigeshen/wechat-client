package me.gaigeshen.wechat.client.core.http;

/**
 * @author gaigeshen
 */
public class WebClientException extends Exception {
  public WebClientException(String message) {
    super(message);
  }
  public WebClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
