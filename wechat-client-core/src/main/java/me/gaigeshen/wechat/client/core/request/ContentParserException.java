package me.gaigeshen.wechat.client.core.request;

/**
 * @author gaigeshen
 */
public class ContentParserException extends Exception {
  public ContentParserException(String message) {
    super(message);
  }
  public ContentParserException(String message, Throwable cause) {
    super(message, cause);
  }
}
