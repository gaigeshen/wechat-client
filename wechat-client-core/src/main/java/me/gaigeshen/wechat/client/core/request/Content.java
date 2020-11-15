package me.gaigeshen.wechat.client.core.request;

/**
 * @author gaigeshen
 */
public interface Content<R extends Result> {

  Class<R> getResultClass();

}
