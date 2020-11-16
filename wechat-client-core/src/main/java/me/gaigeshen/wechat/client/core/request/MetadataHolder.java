package me.gaigeshen.wechat.client.core.request;

/**
 * @author gaigeshen
 */
class MetadataHolder {

  private final String url;

  private final String method;

  private final boolean requireAccessToken;

  private final boolean json;

  private final boolean urlEncoded;

  private final boolean multipart;

  MetadataHolder(String url, String method, boolean requireAccessToken, boolean json, boolean urlEncoded, boolean multipart) {
    this.url = url;
    this.method = method;
    this.requireAccessToken = requireAccessToken;
    this.json = json;
    this.urlEncoded = urlEncoded;
    this.multipart = multipart;
  }

  public String getUrl() {
    return url;
  }

  public String getMethod() {
    return method;
  }

  public boolean isRequireAccessToken() {
    return requireAccessToken;
  }

  public boolean isJson() {
    return json;
  }

  public boolean isUrlEncoded() {
    return urlEncoded;
  }

  public boolean isMultipart() {
    return multipart;
  }

}
