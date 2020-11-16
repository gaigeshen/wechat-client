package me.gaigeshen.wechat.client.core.request;

import me.gaigeshen.wechat.client.core.http.RequestContent;

/**
 * @author gaigeshen
 */
public interface ContentParser {

  RequestContent parse(Content<?> content) throws ContentParserException;

}
