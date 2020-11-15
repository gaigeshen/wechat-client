package me.gaigeshen.wechat.client.core.request;

/**
 * @author gaigeshen
 */
public interface Request<C> {

  Content getContent();

  String getUrl();

  String getMethod();

  boolean isRequireAccessToken();

}
