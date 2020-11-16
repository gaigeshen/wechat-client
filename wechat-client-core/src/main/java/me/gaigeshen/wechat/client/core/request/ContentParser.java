package me.gaigeshen.wechat.client.core.request;

/**
 * @author gaigeshen
 */
public interface ContentParser<C> {

  C parse(Content<?> content) throws ContentParserException;

}
