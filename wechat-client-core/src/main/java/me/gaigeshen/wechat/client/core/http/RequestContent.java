package me.gaigeshen.wechat.client.core.http;

import org.apache.http.entity.ContentType;

import java.io.InputStream;
import java.util.Map;

/**
 * @author gaigeshen
 */
public interface RequestContent {

  String getUri();

  String getMethod();

  Type getType();

  String getEncoding();

  String getText();

  byte[] getBinary();

  InputStream getStream();

  Map<String, String> getParameters();

  Map<String, Object> getMultipartParameters();

  /**
   * @author gaigeshen
   */
  enum Type {

    TEXT_PLAIN("text/plain"),

    TEXT_JSON("application/json"),

    BINARY("application/octet-stream"),

    STREAM("application/octet-stream"),

    PARAMETERS("application/x-www-form-urlencoded"),

    MULTIPART_PARAMETERS("multipart/form-data");

    private final String value;

    Type(String name) {
      this.value = name;
    }

    public String getName() {
      return value;
    }

    public ContentType parseToContentType() {
      return ContentType.create(value);
    }

    public ContentType parseToContentType(String charset) {
      return ContentType.create(value, charset);
    }

    public Type parseFromName(String name) {
      for (Type type : values()) {
        if (type.value.equals(name)) {
          return type;
        }
      }
      throw new IllegalArgumentException("Invalid name:: " + name);
    }

    @Override
    public String toString() {
      return value;
    }
  }

}
