package com.mng.rpc.proxy;

import com.mng.rpc.client.NettyTmpClient;
import com.mng.rpc.codec.DubboRequest;
import com.mng.rpc.consumer.CTX;
import com.mng.rpc.util.Utils;
import io.netty.util.HashedWheelTimer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

public class ConsumerInvocationHandler implements InvocationHandler {

  private NettyTmpClient client;

  private HashedWheelTimer timer;

  public ConsumerInvocationHandler(NettyTmpClient client) {
    this.client = client;
    this.timer = new HashedWheelTimer();
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    String name = method.getName();
    Class<?> returnType = method.getReturnType();
    Class<?>[] parameterTypes = method.getParameterTypes();

    Class<?> declaringClass = method.getDeclaringClass();
    String path = declaringClass.getName();
    String desc = Utils.getDesc(parameterTypes);

    DubboRequest request = new DubboRequest(path, name, returnType, desc, args);
    CompletableFuture<Object> future = CTX.newFuture(request);

    try {
      client.send(request);
    } catch (Exception e) {
      CTX.removeFuture(request.getId());
      future.cancel(true);
    }
    try {
      return future.get();
    } catch (Exception e) {
      throw e.getCause();
    }
  }
}
