package me.gaigeshen.wechat.client.core.request;

/**
 * @author gaigeshen
 */
public interface Request {

  String METHOD_GET = "get";

  String METHOD_POST = "post";

  default boolean isGetMethod() {
    return METHOD_GET.equalsIgnoreCase(getMethod());
  }
  default boolean isPostMethod() {
    return METHOD_POST.equalsIgnoreCase(getMethod());
  }


  String getMethod();

  String getContentType();

  String getUrl();

  String getAppid();

}
