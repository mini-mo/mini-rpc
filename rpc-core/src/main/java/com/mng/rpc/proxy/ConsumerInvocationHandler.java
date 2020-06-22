package com.mng.rpc.proxy;

import com.mng.rpc.client.NettyTmpClient;
import com.mng.rpc.codec.DubboRequest;
import com.mng.rpc.consumer.CTX;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

public class ConsumerInvocationHandler implements InvocationHandler {

  private NettyTmpClient client;

  public ConsumerInvocationHandler(NettyTmpClient client) {
    this.client = client;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    String name = method.getName();
    Class<?> returnType = method.getReturnType();
    Class<?>[] parameterTypes = method.getParameterTypes();
    Class<?> declaringClass = method.getDeclaringClass();

    String path = declaringClass.getName();
    StringBuilder sb = new StringBuilder();
    for (Class<?> parameterType : parameterTypes) {
      sb.append("L").append(parameterType.getName().replace('.', '/')).append(";");
    }
    String desc = sb.toString();

    DubboRequest request = new DubboRequest(path, name, desc, args);
    CompletableFuture<Object> future = CTX.newFuture(request);
    try {
      client.send(request);
    } catch (Exception e) {
      CTX.removeFuture(request.getId());
      future.cancel(true);
    }
    return future.get();
  }
}
