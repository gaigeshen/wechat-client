package me.gaigeshen.wechat.client.core.http;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author gaigeshen
 */
public interface ResponseContent {

  byte[] getRawBytes();

  String getType();

  String getAsString();

  String getAsString(Charset charset);

  InputStream getAsStream();

}
