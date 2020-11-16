package me.gaigeshen.wechat.client.core.request;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author gaigeshen
 */
public abstract class AbstractContentParser<C> implements ContentParser<C> {

  protected Map<String, Object> getFieldValues(Content<?> content, boolean snakeFieldNames) throws ContentParserException {
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
      throw new ContentParserException("Could not get content field values:: " + content, e);
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
