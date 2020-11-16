package me.gaigeshen.wechat.client.core.http;

import me.gaigeshen.wechat.client.core.util.Asserts;
import org.apache.http.client.fluent.Content;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author gaigeshen
 */
public class ResponseContentImpl implements ResponseContent {

  private final Content content;

  public ResponseContentImpl(Content content) {
    this.content = Asserts.notNull(content, "content");
  }

  @Override
  public byte[] getRawBytes() {
    return content.asBytes();
  }

  @Override
  public String getContentType() {
    return content.getType().getMimeType();
  }

  @Override
  public String getAsString() {
    return content.asString();
  }

  @Override
  public String getAsString(Charset charset) {
    return content.asString(charset);
  }

  @Override
  public InputStream getAsStream() {
    return content.asStream();
  }
}
