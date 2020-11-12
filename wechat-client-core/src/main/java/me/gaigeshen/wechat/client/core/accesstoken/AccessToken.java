package me.gaigeshen.wechat.client.core.accesstoken;

import me.gaigeshen.wechat.client.core.util.Asserts;

import java.util.Date;

/**
 * 访问令牌
 *
 * @author gaigeshen
 */
public class AccessToken {

  private final String appid; // 应用编号

  private final String accessToken; // 访问令牌值

  private final long expiresIn; // 有效期单位秒

  private final long expiresTimestamp; // 过期时间点单位秒

  private final Date updateTime; // 更新时间

  private AccessToken(Builder builder) {
    this.appid = builder.appid;
    this.accessToken = builder.accessToken;
    this.expiresIn = builder.expiresIn;
    this.expiresTimestamp = builder.expiresTimestamp;
    this.updateTime = builder.updateTime;
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getAppid() {
    return appid;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public long getExpiresIn() {
    return expiresIn;
  }

  public long getExpiresTimestamp() {
    return expiresTimestamp;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  /**
   * @author gaigeshen
   */
  public static class Builder {

    private String appid;

    private String accessToken;

    private long expiresIn;

    private long expiresTimestamp;

    private Date updateTime;

    private Builder() { }

    public Builder setAppid(String appid) {
      this.appid = Asserts.notBlank(appid, "appid");
      return this;
    }

    public Builder setAccessToken(String accessToken) {
      this.accessToken = Asserts.notBlank(accessToken, "accessToken");
      return this;
    }

    public Builder setExpiresIn(long expiresIn) {
      this.expiresIn = Asserts.notNegative(expiresIn, "expiresIn");
      return this;
    }

    public Builder setExpiresTimestamp(long expiresTimestamp) {
      this.expiresTimestamp = Asserts.notNegative(expiresTimestamp, "expiresTimestamp");
      return this;
    }

    public Builder setUpdateTime(Date updateTime) {
      this.updateTime = Asserts.notNull(updateTime, "updateTime");
      return this;
    }

    public AccessToken build() {
      return new AccessToken(this);
    }
  }

}
