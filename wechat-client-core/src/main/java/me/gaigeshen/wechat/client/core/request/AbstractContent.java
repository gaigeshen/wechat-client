package me.gaigeshen.wechat.client.core.request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author gaigeshen
 */
public abstract class AbstractContent<R extends Result> implements Content<R> {

  private final Class<R> resultClass;

  @SuppressWarnings("unchecked")
  protected AbstractContent() {
    Type genericSuperclass = getClass().getGenericSuperclass();
    if (genericSuperclass instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
      if (parameterizedType.getRawType().equals(AbstractContent.class)) {
        resultClass = (Class<R>) parameterizedType.getActualTypeArguments()[0];
      }
    }
    throw new IllegalStateException("Could not initialize content because cannot determine result class:: ");
  }


  @Override
  public final Class<R> getResultClass() {
    return resultClass;
  }
}
