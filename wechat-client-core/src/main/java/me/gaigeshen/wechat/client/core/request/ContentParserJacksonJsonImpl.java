package me.gaigeshen.wechat.client.core.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.gaigeshen.wechat.client.core.util.Asserts;
import me.gaigeshen.wechat.client.core.util.JsonUtils;

import java.util.Objects;

/**
 * @author gaigeshen
 */
public class ContentParserJacksonJsonImpl implements ContentParser<String> {

  private final ObjectMapper objectMapper;

  private ContentParserJacksonJsonImpl() {
    this.objectMapper = null;
  }

  private ContentParserJacksonJsonImpl(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public static ContentParserJacksonJsonImpl create() {
    return new ContentParserJacksonJsonImpl();
  }

  public static ContentParserJacksonJsonImpl create(ObjectMapper objectMapper) {
    return new ContentParserJacksonJsonImpl(Asserts.notNull(objectMapper, "objectMapper"));
  }

  @Override
  public String parse(Content content) throws ContentParserException {
    Asserts.notNull(content, "content");
    try {
      if (Objects.isNull(objectMapper)) {
        return JsonUtils.toJson(content);
      }
      return JsonUtils.toJson(content, objectMapper);
    } catch (Exception e) {
      throw new ContentParserException("Could not parse to json string:: " + content.getClass(), e);
    }
  }
}
