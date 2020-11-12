package me.gaigeshen.wechat.client.core.request;

/**
 * @author gaigeshen
 */
public class DefaultRequest implements Request {

  private final String method;

  private final String contentType;

  private final String url;

  private final String appid;

  private DefaultRequest(String method, String contentType, String url, String appid) {
    this.method = method;
    this.contentType = contentType;
    this.url = url;
    this.appid = appid;
  }

  public static DefaultRequest createGet(String contentType, String url, String appid) {
    return new DefaultRequest(METHOD_GET, contentType, url, appid);
  }



  @Override
  public String getMethod() {
    return method;
  }

  @Override
  public String getContentType() {
    return contentType;
  }

  @Override
  public String getUrl() {
    return url;
  }

  @Override
  public String getAppid() {
    return appid;
  }
}
