package me.gaigeshen.wechat.client.core.request;

import com.fasterxml.jackson.databind.JsonNode;
import me.gaigeshen.wechat.client.core.util.JsonUtils;

import java.util.Map;
import java.util.Objects;

/**
 * 默认的响应结果
 *
 * @author gaigeshen
 */
public class DefaultResponse implements Response {

  private final String rawString;

  private final JsonNode jsonNode;

  private final boolean success;

  private final String message;

  private DefaultResponse(String rawString, JsonNode jsonNode, boolean success, String message) {
    this.rawString = rawString;
    this.jsonNode = jsonNode;
    this.success = success;
    this.message = message;
  }

  public static DefaultResponse create(String rawString) throws ResponseParseException {
    try {
      JsonNode jsonNode = parseJsonNode(rawString);
      return new DefaultResponse(rawString, jsonNode, confirmSuccessStatus(jsonNode), confirmMessageStatus(jsonNode));
    } catch (Exception e) {
      throw new ResponseParseException("Cannot create response:: raw string " + rawString, e);
    }
  }

  private static JsonNode parseJsonNode(String rawString) {
    return JsonUtils.parseJsonNode(rawString);
  }

  private static boolean confirmSuccessStatus(JsonNode rawJsonNode) {
    JsonNode errcodeJsonNode = rawJsonNode.get("errcode");
    if (Objects.isNull(errcodeJsonNode)) {
      return true;
    }
    return JsonUtils.parseIntValue(errcodeJsonNode) == 0;
  }

  private static String confirmMessageStatus(JsonNode rawJsonNode) {
    JsonNode errmsgJsonNode = rawJsonNode.get("errmsg");
    if (Objects.isNull(errmsgJsonNode)) {
      return "";
    }
    return JsonUtils.parseStringValue(errmsgJsonNode);
  }

  @Override
  public String getRawString() {
    return rawString;
  }

  @Override
  public String getResultRawString() {
    return rawString;
  }

  @Override
  public boolean isFailed() {
    return !success;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public Map<String, Object> parseMapping() throws ResponseParseException {
    try {
      return JsonUtils.parseMapping(jsonNode);
    } catch (Exception e) {
      throw new ResponseParseException("Cannot parse to mapping:: " + rawString);
    }
  }
}
