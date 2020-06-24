package com.mng.rpc.consumer;

import com.mng.rpc.codec.DubboRequest;
import com.mng.rpc.codec.DubboResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class CTX {

  private static final Map<Long, RpcCompletableFuture<Object>> FUTURES = new ConcurrentHashMap<>();

  private static Executor executor = Executors.newSingleThreadExecutor();

  public static CompletableFuture<Object> newFuture(DubboRequest request) {
    RpcCompletableFuture future = new RpcCompletableFuture(request);
    FUTURES.put(request.getId(), future);
    return future;
  }

  public static void removeFuture(Long id) {
    FUTURES.remove(id);
  }

  public static Class<?> getReturnType(Long id) {
    RpcCompletableFuture<Object> future = FUTURES.get(id);
    if (future == null) {
      return null;
    }
    return future.getRequest().getReturnType();
  }

  public static void release(DubboResponse msg) {
    RpcCompletableFuture<Object> future = FUTURES.get(msg.id);
    future.complete(msg.result);
    FUTURES.remove(msg.id);
  }
}
