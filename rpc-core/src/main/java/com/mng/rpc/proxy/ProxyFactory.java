package com.mng.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public abstract class ProxyFactory {

  public static <T> T newProxy(Class<T> T, InvocationHandler handler) {
    Object instance = Proxy
        .newProxyInstance(ProxyFactory.class.getClassLoader(), new Class[]{T},
            handler);
    return (T) instance;
  }
}
