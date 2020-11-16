package me.gaigeshen.wechat.client.core.request;

import me.gaigeshen.wechat.client.core.util.Asserts;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gaigeshen
 */
public class ContentParserUrlEncodedImpl extends AbstractContentParser<Map<String, String>> {

  private final boolean snakeContentFields;

  public ContentParserUrlEncodedImpl() {
    this.snakeContentFields = true;
  }

  public ContentParserUrlEncodedImpl(boolean snakeContentFields) {
    this.snakeContentFields = snakeContentFields;
  }

  @Override
  public Map<String, String> parse(Content<?> content) throws ContentParserException {
    Asserts.notNull(content, "content");
    Map<String, Object> fieldValues = getFieldValues(content, snakeContentFields);
    if (fieldValues.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<String, String> result = new HashMap<>();
    for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
      result.put(entry.getKey(), String.valueOf(entry.getValue()));
    }
    return result;
  }
}
