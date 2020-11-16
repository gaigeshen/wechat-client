package me.gaigeshen.wechat.client.core.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.gaigeshen.wechat.client.core.http.RequestContent;
import me.gaigeshen.wechat.client.core.http.RequestContentImpl;
import me.gaigeshen.wechat.client.core.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author gaigeshen
 */
public class ContentParserImpl implements ContentParser {

  private static final boolean DEFAULT_SNAKE_FIELD_NAMES = true;

  private static final String DEFAULT_ENCODING = "utf-8";

  private static final Map<Class<?>, Metadata> metadatas = new ConcurrentHashMap<>();

  private boolean snakeFieldNames = DEFAULT_SNAKE_FIELD_NAMES;

  private String encoding = DEFAULT_ENCODING;

  private ObjectMapper objectMapper;

  public ContentParserImpl() {
  }

  public ContentParserImpl(boolean snakeFieldNames, String encoding) {
    this.snakeFieldNames = snakeFieldNames;
    this.encoding = encoding;
  }

  @Override
  public RequestContent parse(Content<?> content) throws ContentParserException {
    Metadata metadata = getValidMetadata(content);
    RequestContentImpl.Builder builder = null;
    if (metadata.isJson()) {
      builder = createJsonRequestContent(content);
    } else if (metadata.isUrlEncoded()) {
      builder = createUrlEncodedRequestContent(content);
    } else if (metadata.isMultipart()) {
      builder = createMultipartRequestContent(content);
    }
    if (Objects.isNull(builder)) {
      throw new ContentParserException("Invalid content metadata:: " + content.getClass());
    }
    return builder.setUri(metadata.getUrl()).setMethod(metadata.getMethod()).setEncoding(encoding).build();
  }

  private RequestContentImpl.Builder createJsonRequestContent(Content<?> content) {
    String json = Objects.isNull(objectMapper) ? JsonUtils.toJson(content)
            : JsonUtils.toJson(content, objectMapper);
    return RequestContentImpl.createJson(json);
  }

  private RequestContentImpl.Builder createUrlEncodedRequestContent(Content<?> content) throws ContentParserException {
    Map<String, Object> fieldValues = getFieldValues(content);
    Map<String, String> parameters = new HashMap<>();
    for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
      parameters.put(entry.getKey(), String.valueOf(entry.getValue()));
    }
    return RequestContentImpl.createParameters(parameters);
  }

  private RequestContentImpl.Builder createMultipartRequestContent(Content<?> content) throws ContentParserException {
    Map<String, Object> fieldValues = getFieldValues(content);
    return RequestContentImpl.createMultipartParameters(fieldValues);
  }

  protected Metadata getValidMetadata(Content<?> content) throws ContentParserException {
    Metadata metadata = getMetadata(content);
    if (!checkMetadata(metadata)) {
      throw new ContentParserException("No such metadata or metadata invalid:: " + content.getClass());
    }
    return metadata;
  }

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

  protected Map<String, Object> getFieldValues(Content<?> content) throws ContentParserException {
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
