package me.gaigeshen.wechat.client.core.accesstoken;

import me.gaigeshen.wechat.client.core.util.Asserts;

/**
 * @author gaigeshen
 */
public class AccessTokenHelper {

  private final AccessToken accessToken;

  public AccessTokenHelper(AccessToken accessToken) {
    this.accessToken = Asserts.notNull(accessToken, "accessToken");
  }

  public boolean isExpired() {
    return accessToken.getExpiresTimestamp() > System.currentTimeMillis() / 1000;
  }

}
