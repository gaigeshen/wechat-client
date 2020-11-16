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

    public ContentType createContentType() {
      return ContentType.create(value);
    }

    public ContentType createContentType(String charset) {
      return ContentType.create(value, charset);
    }

    @Override
    public String toString() {
      return value;
    }
  }

}
