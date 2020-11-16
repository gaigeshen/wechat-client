package me.gaigeshen.wechat.client.core.request;

import java.lang.annotation.*;

/**
 * @author gaigeshen
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Metadata {

  String url() default "";

  String method() default "post";

  boolean requireAccessToken() default true;

  boolean json() default true;

  boolean urlEncoded() default false;

  boolean multipart() default false;

}
