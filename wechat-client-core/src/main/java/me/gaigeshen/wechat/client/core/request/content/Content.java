package me.gaigeshen.wechat.client.core.request.content;

import me.gaigeshen.wechat.client.core.request.result.Result;

/**
 * Request content data
 *
 * @author gaigeshen
 */
public interface Content<R extends Result> {
  /**
   * Returns result class
   *
   * @return Result class
   */
  Class<R> getResultClass();

}
