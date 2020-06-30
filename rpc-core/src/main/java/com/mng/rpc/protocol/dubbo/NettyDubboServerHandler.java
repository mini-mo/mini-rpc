package com.mng.rpc.protocol.dubbo;

import com.mng.rpc.codec.DubboRequest;
import com.mng.rpc.codec.DubboResponse;
import com.mng.rpc.registry.LocalRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NettyDubboServerHandler extends SimpleChannelInboundHandler<DubboRequest> {

  private ExecutorService pool = new ThreadPoolExecutor(10, 10,
      0L, TimeUnit.MILLISECONDS,
      new SynchronousQueue<>());

  public NettyDubboServerHandler(NettyServer nettyServer) {
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, DubboRequest msg) throws Exception {
    String className = msg.getPath();
    String methodName = msg.getMethod();

    String finalClassName = className;
    Class clazz = LocalRegistry.getInstance().allKeys().stream()
        .filter(it -> Objects.equals(it.getName(), finalClassName))
        .findFirst().orElse(null);

    if (clazz == null) {
      throw new IllegalStateException();
    }

    Method method = Arrays.stream(clazz.getDeclaredMethods())
        .filter(it -> Objects.equals(it.getName(), methodName))
        .findFirst().orElse(null);

    if (method == null) {
      throw new IllegalStateException();
    }

    // 走业务线程池
    CompletableFuture<Void> future = CompletableFuture.supplyAsync(
        () -> {
          try {
            return method.invoke(LocalRegistry.getInstance().get(clazz), msg.getArgs());
          } catch (Exception e) {
            throw new IllegalStateException();
          }
        }, pool)
        .exceptionally(ex -> null)
        .thenAccept(result -> ctx.writeAndFlush(new DubboResponse(msg.getId(), result)));

    // 加了业务线程池。。。吞吐量下降 15%
//    Object result = method.invoke(LocalRegistry.getInstance().get(clazz), msg.getArgs());
//    ctx.writeAndFlush(new DubboResponse(msg.getId(), result));
  }
}
