package me.gaigeshen.wechat.client.core.request;

/**
 * @author gaigeshen
 */
public class DefaultRequest implements Request<String> {



  @Override
  public Content getContent() {
    return null;
  }

  @Override
  public String getUrl() {
    return null;
  }

  @Override
  public String getMethod() {
    return null;
  }

  @Override
  public boolean isRequireAccessToken() {
    return false;
  }

}
