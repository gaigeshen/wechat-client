package me.gaigeshen.wechat.client.core.request;

/**
 * @author gaigeshen
 */
public interface ContentParser<C> {

  String getContentType();

  String getContentEncoding();

  C parse(Content<?> content) throws ContentParserException;

}
