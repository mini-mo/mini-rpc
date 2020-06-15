package com.mng.rpc.consumer;

import com.mng.rpc.codec.DubboRequest;
import com.mng.rpc.codec.DubboResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class CTX {

  private static final Map<Long, CompletableFuture<Object>> FUTURES = new ConcurrentHashMap<>();

  private static Executor executor = Executors.newSingleThreadExecutor();

  public static CompletableFuture<Object> newFuture(DubboRequest request) {
    CompletableFuture<Object> future = new CompletableFuture<>();
    FUTURES.put(request.getId(), future);
    return future;
  }

  public static void removeFuture(Long id) {
    FUTURES.remove(id);
  }

  public static void release(DubboResponse msg) {
    CompletableFuture<Object> future = FUTURES.get(msg.id);
    future.complete(((String) msg.result));
    FUTURES.remove(msg.id);
  }
}
