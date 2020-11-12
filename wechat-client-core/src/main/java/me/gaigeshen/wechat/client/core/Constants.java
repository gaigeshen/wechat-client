package me.gaigeshen.wechat.client.core;

/**
 * @author gaigeshen
 */
public class Constants {

  public static final String BASE_TEMPLATE_URL = "https://api.weixin.qq.com";

  public static final String ACCESS_TOKEN_TEMPLATE_URL = BASE_TEMPLATE_URL + "/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

  public static final String MENU_CREATE_TEMPLATE_URL = BASE_TEMPLATE_URL + "/cgi-bin/menu/create?access_token=%s";

  public static final String MENU_GET_TEMPLATE_URL = BASE_TEMPLATE_URL + "/cgi-bin/get_current_selfmenu_info?access_token=%s";

  public static final String MENU_DELETE_TEMPLATE_URL = BASE_TEMPLATE_URL + "/cgi-bin/menu/delete?access_token=%s";

  public static final String CONDITIONAL_MENU_CREATE_TEMPLATE_URL = BASE_TEMPLATE_URL + "/cgi-bin/menu/addconditional?access_token=%s";

  public static final String CONDITIONAL_MENU_DELETE_TEMPLATE_URL = BASE_TEMPLATE_URL + "/cgi-bin/menu/delconditional?access_token=%s";

  private Constants() { }

  private static String getUrl(String templateUrl, Object... values) {
    return String.format(templateUrl, values);
  }

  public static String getAccessTokenUrl(String appid, String secret) {
    return getUrl(ACCESS_TOKEN_TEMPLATE_URL, appid, secret);
  }

  public static String getMenuCreateUrl(String accessToken) {
    return getUrl(MENU_CREATE_TEMPLATE_URL, accessToken);
  }

  public static String getMenuGetUrl(String accessToken) {
    return getUrl(MENU_GET_TEMPLATE_URL, accessToken);
  }

  public static String getMenuDeleteUrl(String accessToken) {
    return getUrl(MENU_DELETE_TEMPLATE_URL, accessToken);
  }

  public static String getConditionalMenuCreateUrl(String accessToken) {
    return getUrl(CONDITIONAL_MENU_CREATE_TEMPLATE_URL, accessToken);
  }

  public static String getConditionalMenuDeleteUrl(String accessToken) {
    return getUrl(CONDITIONAL_MENU_DELETE_TEMPLATE_URL, accessToken);
  }
}
