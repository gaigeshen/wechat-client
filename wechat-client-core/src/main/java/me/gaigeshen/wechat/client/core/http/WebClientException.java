package me.gaigeshen.wechat.client.core.http;

/**
 * Web client exception
 *
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
