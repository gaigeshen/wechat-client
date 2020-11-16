package me.gaigeshen.wechat.client.core.request;

import me.gaigeshen.wechat.client.core.util.Asserts;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gaigeshen
 */
public class ContentParserMultipartImpl extends AbstractContentParser<Map<String, Object>> {

  private final boolean snakeContentFields;

  public ContentParserMultipartImpl() {
    this.snakeContentFields = true;
  }

  public ContentParserMultipartImpl(boolean snakeContentFields) {
    this.snakeContentFields = snakeContentFields;
  }

  @Override
  public Map<String, Object> parse(Content<?> content) throws ContentParserException {
    Asserts.notNull(content, "content");
    Map<String, Object> fieldValues = getFieldValues(content, snakeContentFields);
    if (fieldValues.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<String, Object> result = new HashMap<>();
    for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
      result.put(entry.getKey(), entry.getValue());
    }
    return result;
  }
}
