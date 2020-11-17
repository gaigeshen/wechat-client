package me.gaigeshen.wechat.client.core.http;

/**
 * Request content invalid
 *
 * @author gaigeshen
 */
public class InvalidRequestContentException extends WebClientException {
  public InvalidRequestContentException(String message) {
    super(message);
  }
  public InvalidRequestContentException(String message, Throwable cause) {
    super(message, cause);
  }
}
