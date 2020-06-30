package com.mng.rpc.consumer;

import com.mng.rpc.codec.DubboRequest;
import com.mng.rpc.codec.DubboResponse;
import com.mng.rpc.exception.ClientTimeoutExection;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public abstract class CTX {

  private static final Map<Long, RpcCompletableFuture<Object>> FUTURES = new ConcurrentHashMap<>();
  private static final Map<Long, Timeout> TIMEOUTS = new ConcurrentHashMap<>();

  private static HashedWheelTimer timer = new HashedWheelTimer();

  public static CompletableFuture<Object> newFuture(DubboRequest request) {
    RpcCompletableFuture future = new RpcCompletableFuture(request);
    FUTURES.put(request.getId(), future);
    TimerTask task = timeout -> {
      if (future.isDone() || future.isCompletedExceptionally() || future.isCancelled()) {
        return;
      }
      future.completeExceptionally(new ClientTimeoutExection());
      CTX.removeFuture(request.getId());
      TIMEOUTS.remove(request.getId());
    };
    Timeout timeout = timer.newTimeout(task, 2000, TimeUnit.MILLISECONDS);
    TIMEOUTS.put(request.getId(), timeout);

    return future;
  }

  public static void removeFuture(Long id) {
    Timeout timeout = TIMEOUTS.get(id);
    if (timeout != null) {
      timeout.cancel();
      TIMEOUTS.remove(id);
    }
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
    if (future == null) {
      return;
    }
    future.complete(msg.result);
    FUTURES.remove(msg.id);
    TIMEOUTS.remove(msg.id);
  }

  public static void shutdown() {
    timer.stop();
  }
}
