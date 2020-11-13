package me.gaigeshen.wechat.client.core.request;

/**
 * @author gaigeshen
 */
public interface Request<C> {

  Content getContent();

  C getSerializedContent();

  String getContentType();

  String getContentEncoding();

  String getUrl();

  String getMethod();

  boolean isRequireAccessToken();

  Class<? extends Result> getResultClass();

}
