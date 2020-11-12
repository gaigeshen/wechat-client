package me.gaigeshen.wechat.client.core.accesstoken;

import me.gaigeshen.wechat.client.core.util.Asserts;

/**
 * 应用配置
 *
 * @author gaigeshen
 */
public class AppConfig {

  private final String appid;

  private final String secret;

  private AppConfig(Builder builder) {
    this.appid = builder.appid;
    this.secret = builder.secret;
  }

  public String getAppid() {
    return appid;
  }

  public String getSecret() {
    return secret;
  }

  public static Builder builder() {
    return new Builder();
  }

  /**
   * @author gaigeshen
   */
  public static class Builder {

    private String appid;

    private String secret;

    public Builder setAppid(String appid) {
      this.appid = Asserts.notBlank(appid, "appid");
      return this;
    }

    public Builder setSecret(String secret) {
      this.secret = Asserts.notBlank(secret, "secret");
      return this;
    }

    public AppConfig build() {
      return new AppConfig(this);
    }
  }

}
