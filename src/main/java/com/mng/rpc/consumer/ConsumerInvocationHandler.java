package com.mng.rpc.consumer;

import com.mng.rpc.client.NettyTmpClient;
import com.mng.rpc.codec.DubboRequest;
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
    DubboRequest request = new DubboRequest("com.mng.rpc.server.HelloService", "hello", args);
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
