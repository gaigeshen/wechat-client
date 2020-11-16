package me.gaigeshen.wechat.client.core.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.gaigeshen.wechat.client.core.util.Asserts;
import me.gaigeshen.wechat.client.core.util.JsonUtils;

/**
 * @author gaigeshen
 */
public class ContentParserJacksonJsonImpl implements ContentParser<String> {

  private static final String DEFAULT_CONTENT_TYPE = "application/json";

  private static final String DEFAULT_CONTENT_ENCODING = "utf-8";

  private final ObjectMapper objectMapper;

  private final String contentEncoding;

  private ContentParserJacksonJsonImpl(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
    this.contentEncoding = DEFAULT_CONTENT_ENCODING;
  }

  private ContentParserJacksonJsonImpl(ObjectMapper objectMapper, String contentEncoding) {
    this.objectMapper = objectMapper;
    this.contentEncoding = contentEncoding;
  }

  public static ContentParserJacksonJsonImpl create(ObjectMapper objectMapper) {
    return new ContentParserJacksonJsonImpl(Asserts.notNull(objectMapper, "objectMapper"));
  }

  public static ContentParserJacksonJsonImpl create(ObjectMapper objectMapper, String contentEncoding) {
    return new ContentParserJacksonJsonImpl(Asserts.notNull(objectMapper, "objectMapper"), Asserts.notBlank(contentEncoding, "contentEncoding"));
  }

  @Override
  public String getContentType() {
    return DEFAULT_CONTENT_TYPE;
  }

  @Override
  public String getContentEncoding() {
    return contentEncoding;
  }

  @Override
  public String parse(Content<?> content) throws ContentParserException {
    Asserts.notNull(content, "content");
    try {
      return JsonUtils.toJson(content, objectMapper);
    } catch (Exception e) {
      throw new ContentParserException("Could not parse to json string:: " + content.getClass(), e);
    }
  }
}
