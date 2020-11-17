package me.gaigeshen.wechat.client.core.request.content;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Helper for content
 *
 * @author gaigeshen
 */
public class ContentHelper {

  // Holds all metadatas
  private static final Map<Class<?>, Metadata> metadatas = new ConcurrentHashMap<>();

  // Producer function for produce metadata
  // Returns null if no @MetadataAttributes annotation at content class
  private static final Function<Class<?>, Metadata> medatataProducer = contentClass -> {
    MetadataAttributes metadataAttributes = contentClass.getAnnotation(MetadataAttributes.class);
    if (Objects.isNull(metadataAttributes)) {
      return null;
    }
    return Metadata.create()
            .setUrl(metadataAttributes.url()).setMethod(metadataAttributes.method())
            .setRequireAccessToken(metadataAttributes.requireAccessToken())
            .setJson(metadataAttributes.json()).setUrlEncoded(metadataAttributes.urlEncoded())
            .setMultipart(metadataAttributes.multipart())
            .build();
  };

  /**
   * Returns metadata of content class
   *
   * @param content The content object
   * @return The metadata
   */
  public static Metadata getMetadata(Content<?> content) {
    return metadatas.computeIfAbsent(content.getClass(), medatataProducer);
  }

  /**
   * Returns all field values of content
   *
   * @param content The content
   * @param snakeFieldNames This parameter can be set to true for returns snake field name
   * @return All field values
   * @throws IllegalAccessException Cannot access content field
   */
  public static Map<String, Object> getFieldValues(Content<?> content, boolean snakeFieldNames) throws IllegalAccessException {
    List<Field> fields = getFields(content.getClass());
    if (fields.isEmpty()) {
      return Collections.emptyMap();
    }
    Map<String, Object> fieldValues = new HashMap<>();
    for (Field field : fields) {
      Object fieldValue = field.get(content);
      if (Objects.isNull(fieldValue)) {
        continue;
      }
      field.setAccessible(true);
      fieldValues.put(snakeFieldNames ? getSnakeFieldName(field.getName()) : field.getName(), fieldValue);
    }
    return fieldValues;
  }

  /**
   * Returns all fields of content class, include all parent class of content class
   *
   * @param contentClass Content class
   * @return All fields
   */
  private static List<Field> getFields(Class<?> contentClass) {
    List<Field> allFields = new ArrayList<>();
    Class<?> currentClass = contentClass;
    while (currentClass != null) {
      Collections.addAll(allFields, currentClass.getDeclaredFields());
      currentClass = currentClass.getSuperclass();
    }
    return allFields;
  }

  /**
   * Returns snake field name
   *
   * @param fieldName The field name
   * @return Snake field name
   */
  private static String getSnakeFieldName(String fieldName) {
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
