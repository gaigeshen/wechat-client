package me.gaigeshen.wechat.client.core.request;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gaigeshen
 */
public abstract class AbstractContentParser<C> implements ContentParser<C> {

  private static final Map<Class<?>, Metadata> metadatas = new ConcurrentHashMap<>();

  protected boolean checkMetadata(Metadata metadata) {
    return Objects.nonNull(metadata) && (!StringUtils.isAnyBlank(metadata.getUrl(), metadata.getMethod()));
  }

  protected Metadata getMetadata(Content<?> content) {
    return metadatas.computeIfAbsent(content.getClass(), contentClass -> {
      MetadataAttributes metadataAttributes = contentClass.getAnnotation(MetadataAttributes.class);
      if (Objects.isNull(metadataAttributes)) {
        return null;
      }
      return Metadata.create()
              .setUrl(metadataAttributes.url())
              .setMethod(metadataAttributes.method())
              .setRequireAccessToken(metadataAttributes.requireAccessToken())
              .setJson(metadataAttributes.json())
              .setUrlEncoded(metadataAttributes.urlEncoded())
              .setMultipart(metadataAttributes.multipart())
              .build();
    });
  }

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
