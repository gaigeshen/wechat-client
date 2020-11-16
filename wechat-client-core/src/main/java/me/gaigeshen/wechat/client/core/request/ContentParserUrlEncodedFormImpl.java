package me.gaigeshen.wechat.client.core.request;

import me.gaigeshen.wechat.client.core.util.Asserts;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author gaigeshen
 */
public class ContentParserUrlEncodedFormImpl implements ContentParser<String> {

  private static final String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";

  private static final String DEFAULT_CONTENT_ENCODING = "utf-8";

  private final String contentEncoding;

  private final boolean snakeContentFields;

  private ContentParserUrlEncodedFormImpl() {
    this.contentEncoding = DEFAULT_CONTENT_ENCODING;
    this.snakeContentFields = true;
  }

  private ContentParserUrlEncodedFormImpl(String contentEncoding, boolean snakeContentFields) {
    this.contentEncoding = contentEncoding;
    this.snakeContentFields = snakeContentFields;
  }

  public static ContentParserUrlEncodedFormImpl create() {
    return new ContentParserUrlEncodedFormImpl();
  }

  public static ContentParserUrlEncodedFormImpl create(String contentEncoding, boolean snakeContentFields) {
    return new ContentParserUrlEncodedFormImpl(Asserts.notBlank(contentEncoding, "encoding"), snakeContentFields);
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
    Map<String, Object> fieldValues = getFieldValues(content, snakeContentFields);
    if (fieldValues.isEmpty()) {
      return "";
    }
    List<NameValuePair> nameValuePairs = new ArrayList<>();
    for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
      nameValuePairs.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
    }
    return URLEncodedUtils.format(nameValuePairs, contentEncoding);
  }

  private Map<String, Object> getFieldValues(Content content, boolean snakeFieldNames) throws ContentParserException {
    List<Field> fields = getFields(content.getClass());
    if (fields.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<String, Object> fieldValues = new HashMap<>();
    try {
      for (Field field : fields) {
        Object fieldValue = field.get(content);
        if (Objects.isNull(fieldValue)) {
          continue;
        }
        field.setAccessible(true);
        fieldValues.put(snakeFieldNames ? getSnakeFieldName(field.getName()) : field.getName(), fieldValue);
      }
    } catch (IllegalAccessException e) {
      throw new ContentParserException("Could not get content field values:: " + content.getClass(), e);
    }
    return fieldValues;
  }

  private List<Field> getFields(Class<?> contentClass) {
    List<Field> allFields = new ArrayList<>();
    Class<?> currentClass = contentClass;
    while (currentClass != null) {
      Collections.addAll(allFields, currentClass.getDeclaredFields());
      currentClass = currentClass.getSuperclass();
    }
    return allFields;
  }

  private String getSnakeFieldName(String fieldName) {
    StringBuilder result = new StringBuilder();
    char[] arr = fieldName.toCharArray();
    int index = 0;
    for (char chr : arr) {
      char cur = chr;
      if (cur >= 65 && cur <= 90) {
        cur += 32;
        if (index != 0) {
          result.append("_");
        }
      }
      result.append(cur);
      index++;
    }
    return result.toString();
  }

}
