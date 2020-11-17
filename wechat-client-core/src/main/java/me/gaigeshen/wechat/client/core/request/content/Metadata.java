package me.gaigeshen.wechat.client.core.request.content;

/**
 * Content class metadata
 *
 * @author gaigeshen
 */
public class Metadata {

  private final String url;

  private final String method;

  private final boolean requireAccessToken;

  private final boolean json;

  private final boolean urlEncoded;

  private final boolean multipart;

  private Metadata(Builder builder) {
    this.url = builder.url;
    this.method = builder.method;
    this.requireAccessToken = builder.requireAccessToken;
    this.json = builder.json;
    this.urlEncoded = builder.urlEncoded;
    this.multipart = builder.multipart;
  }

  public static Builder create() {
    return new Builder();
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

  /**
   * Builder for build metadata
   *
   * @author gaigeshen
   */
  public static class Builder {

    private String url;

    private String method;

    private boolean requireAccessToken;

    private boolean json;

    private boolean urlEncoded;

    private boolean multipart;

    public Builder setUrl(String url) {
      this.url = url;
      return this;
    }

    public Builder setMethod(String method) {
      this.method = method;
      return this;
    }

    public Builder setRequireAccessToken(boolean requireAccessToken) {
      this.requireAccessToken = requireAccessToken;
      return this;
    }

    public Builder setJson(boolean json) {
      this.json = json;
      return this;
    }

    public Builder setUrlEncoded(boolean urlEncoded) {
      this.urlEncoded = urlEncoded;
      return this;
    }

    public Builder setMultipart(boolean multipart) {
      this.multipart = multipart;
      return this;
    }

    public Metadata build() {
      return new Metadata(this);
    }
  }
}
