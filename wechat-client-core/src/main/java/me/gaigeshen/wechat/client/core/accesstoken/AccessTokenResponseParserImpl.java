package me.gaigeshen.wechat.client.core.accesstoken;

import me.gaigeshen.wechat.client.core.request.RequestResultException;
import me.gaigeshen.wechat.client.core.request.Response;
import me.gaigeshen.wechat.client.core.request.ResponseParseException;
import me.gaigeshen.wechat.client.core.request.ResponseParser;
import me.gaigeshen.wechat.client.core.util.Asserts;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 访问令牌请求响应结果转换器，用于将请求响应结果转换为访问令牌
 *
 * @author gaigeshen
 */
public class AccessTokenResponseParserImpl implements ResponseParser<AccessToken> {
  @Override
  public AccessToken parse(String appid, Response response) throws ResponseParseException, RequestResultException {
    Asserts.notBlank(appid, "appid");
    Asserts.notNull(response, "response");
    if (response.isFailed()) {
      throw new RequestResultException("Could not parse access token from failed response, " + response.getMessage() + "::");
    }
    // 返回的是业务数据部分
    Map<String, Object> accessTokenData = response.parseMapping();
    // 取访问令牌的各个字段
    String accessToken = MapUtils.getString(accessTokenData, "access_token");
    Long expiresIn = MapUtils.getLong(accessTokenData, "expires_in");
    // 访问令牌必需的数据进行判断
    if (StringUtils.isBlank(accessToken) || Objects.isNull(expiresIn)) {
      throw new RequestResultException("Could not parse valid access token from response:: " + response.getRawString());
    }
    // 计算过期时间点之后创建并返回访问令牌
    long expiresTimestamp = System.currentTimeMillis() / 1000 + expiresIn;
    return AccessToken.builder()
            .setAppid(appid).setAccessToken(accessToken)
            .setExpiresIn(expiresIn).setExpiresTimestamp(expiresTimestamp)
            .setUpdateTime(new Date())
            .build();
  }
}
