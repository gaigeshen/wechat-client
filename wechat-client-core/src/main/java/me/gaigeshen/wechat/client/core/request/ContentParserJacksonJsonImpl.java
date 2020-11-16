package me.gaigeshen.wechat.client.core.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.gaigeshen.wechat.client.core.util.Asserts;
import me.gaigeshen.wechat.client.core.util.JsonUtils;

import java.util.Objects;

/**
 * @author gaigeshen
 */
public class ContentParserJacksonJsonImpl extends AbstractContentParser<String> {

  private final ObjectMapper objectMapper;

  public ContentParserJacksonJsonImpl() {
    this.objectMapper = null;
  }

  public ContentParserJacksonJsonImpl(ObjectMapper objectMapper) {
    this.objectMapper = Asserts.notNull(objectMapper, "objectMapper");
  }

  @Override
  public String parse(Content<?> content) throws ContentParserException {
    Asserts.notNull(content, "content");
    try {
      if (Objects.nonNull(objectMapper)) {
        return JsonUtils.toJson(content, objectMapper);
      }
      return JsonUtils.toJson(content);
    } catch (Exception e) {
      throw new ContentParserException("Could not parse to json string:: " + content, e);
    }
  }
}
