package me.gaigeshen.wechat.client.core.request;

import me.gaigeshen.wechat.client.core.util.Asserts;

import java.util.Objects;

/**
 * @author gaigeshen
 */
public class MetadataHolder {

  private final Metadata metadata;

  public MetadataHolder(Content content) {
    this.metadata = extractMetadata(Asserts.notNull(content, "content"));
  }

  private Metadata extractMetadata(Content content) {
    Metadata annotation = content.getClass().getAnnotation(Metadata.class);
    if (Objects.isNull(annotation)) {
      throw new IllegalStateException("");
    }
    return annotation;
  }

  public String getUrl() {
    return metadata.url();
  }

  public String getMethod() {
    return metadata.method();
  }

  public boolean isRequireAccessToken() {
    return metadata.requireAccessToken();
  }

  public Class<? extends Result> getResultClass() {
    return metadata.resultClass();
  }
}
