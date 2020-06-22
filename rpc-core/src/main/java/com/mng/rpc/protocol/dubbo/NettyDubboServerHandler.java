package com.mng.rpc.protocol.dubbo;

import com.mng.rpc.codec.DubboRequest;
import com.mng.rpc.codec.DubboResponse;
import com.mng.rpc.registry.LocalRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

public class NettyDubboServerHandler extends SimpleChannelInboundHandler<DubboRequest> {

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

    Object result = method.invoke(LocalRegistry.getInstance().get(clazz), msg.getArgs());
    ctx.writeAndFlush(new DubboResponse(msg.getId(), result));
  }
}
