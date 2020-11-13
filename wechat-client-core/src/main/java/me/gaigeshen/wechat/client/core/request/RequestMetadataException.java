package me.gaigeshen.wechat.client.core.request;

/**
 * @author gaigeshen
 */
public class RequestMetadataException extends Exception {
  public RequestMetadataException(String message) {
    super(message);
  }
  public RequestMetadataException(String message, Throwable cause) {
    super(message, cause);
  }
}
