package me.gaigeshen.wechat.client.core.request;

/**
 * @author gaigeshen
 */
public class RequestMetaDataException extends Exception {
  public RequestMetaDataException(String message) {
    super(message);
  }
  public RequestMetaDataException(String message, Throwable cause) {
    super(message, cause);
  }
}
