package me.gaigeshen.wechat.client.core.request;

/**
 * 请求执行结果异常
 *
 * @author gaigeshen
 */
public class RequestResultException extends RequestExecutionException {

  public RequestResultException(String message) {
    super(message);
  }

  public RequestResultException(String message, Throwable cause) {
    super(message, cause);
  }
}
