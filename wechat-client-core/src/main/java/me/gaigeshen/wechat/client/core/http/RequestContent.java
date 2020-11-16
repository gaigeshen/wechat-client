package me.gaigeshen.wechat.client.core.http;

import java.io.InputStream;
import java.util.Map;

/**
 * @author gaigeshen
 */
public interface RequestContent {

  String getUri();

  String getMethod();

  String getContentType();

  String getContentEncoding();

  String getText();

  byte[] getBinary();

  InputStream getStream();

  Map<String, String> getParameters();

  Map<String, Object> getMultipartParameters();

}
