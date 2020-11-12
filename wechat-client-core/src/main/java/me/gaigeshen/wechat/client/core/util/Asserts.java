package me.gaigeshen.wechat.client.core.util;

import java.util.Collection;
import java.util.Objects;

/**
 * @author gaigeshen
 */
public class Asserts {

  private Asserts() { }

  public static <T> T notNull(T argument, String name) {
    if (Objects.isNull(argument)) {
      throw new IllegalArgumentException(name + " may not be null");
    }
    return argument;
  }

  public static <E, T extends Collection<E>> T notEmpty(T argument, String name) {
    if (Objects.isNull(argument)) {
      throw new IllegalArgumentException(name + " may not be null");
    }
    if (argument.isEmpty()) {
      throw new IllegalArgumentException(name + " may not be empty");
    }
    return argument;
  }

  public static <T extends CharSequence> T notBlank(T argument, String name) {
    if (Objects.nonNull(argument)) {
      for (int i = 0; i < argument.length(); i++) {
        if (!Character.isWhitespace(argument.charAt(i))) {
          return argument;
        }
      }
    }
    throw new IllegalArgumentException(name + " may not be blank");
  }

  public static int notNegative(int n, String name) {
    if (n < 0) {
      throw new IllegalArgumentException(name + " may not be negative");
    }
    return n;
  }

  public static long notNegative(long n, String name) {
    if (n < 0) {
      throw new IllegalArgumentException(name + " may not be negative");
    }
    return n;
  }

  public static int positive(int n, String name) {
    if (n <= 0) {
      throw new IllegalArgumentException(name + " may not be negative or zero");
    }
    return n;
  }

  public static long positive(long n, String name) {
    if (n <= 0) {
      throw new IllegalArgumentException(name + " may not be negative or zero");
    }
    return n;
  }
}
