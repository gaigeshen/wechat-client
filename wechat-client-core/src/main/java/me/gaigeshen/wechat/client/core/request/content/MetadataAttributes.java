package me.gaigeshen.wechat.client.core.request.content;

import java.lang.annotation.*;

/**
 * Metadata annotation
 *
 * @author gaigeshen
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MetadataAttributes {
  /**
   * Returns url
   *
   * @return Request url
   */
  String url() default "";

  /**
   * Returns method, 'get' or 'post'
   *
   * @return Request method
   */
  String method() default "post";

  /**
   * Returns boolean value
   *
   * @return If true, append access token value parameter to the url before execute request
   */
  boolean requireAccessToken() default true;

  /**
   * Returns boolean value
   *
   * @return If true, the request content type is text json
   */
  boolean json() default true;

  /**
   * Returns boolean value
   *
   * @return If true, the request content type is url encoded parameters
   */
  boolean urlEncoded() default false;

  /**
   * Returns boolean value
   *
   * @return If true, the request content type is multipart parameters
   */
  boolean multipart() default false;

}
