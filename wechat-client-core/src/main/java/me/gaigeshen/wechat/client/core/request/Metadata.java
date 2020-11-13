package me.gaigeshen.wechat.client.core.request;

import java.lang.annotation.*;

/**
 * @author gaigeshen
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Metadata {

  String url();

  String method() default "post";

  boolean requireAccessToken() default true;

  Class<? extends Result> resultClass();

}
