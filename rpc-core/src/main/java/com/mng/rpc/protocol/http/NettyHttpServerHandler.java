package com.mng.rpc.protocol.http;

import com.mng.rpc.protocol.telnet.TextInvoke;
import com.mng.rpc.registry.LocalRegistry;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

@Sharable
public class NettyHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

  private NettyServer server;

  public NettyHttpServerHandler(NettyServer server) {
    this.server = server;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
    String uri = req.uri();

    int len = req.content().readableBytes();
    byte[] bytes = new byte[len];
    req.content().readBytes(bytes, 0, len);
    String str = new String(bytes);

    TextInvoke invoke = parseInvoke(uri, str);

    if (invoke == null) {
      respErr(ctx, "not found valid provider");
      return;
    }

    Class clazz = LocalRegistry.getInstance().allKeys().stream()
        .filter(it -> Objects.equals(it.getName(), invoke.clazz))
        .findFirst().orElse(null);

    if (clazz == null) {
      respErr(ctx, "not found valid provider");
      return;
    }

    Method method = Arrays.stream(clazz.getDeclaredMethods())
        .filter(it -> Objects.equals(it.getName(), invoke.method))
        .findFirst().orElse(null);
    if (method == null) {
      respErr(ctx, "not found valid provider");
      return;
    }

    try {
      Object ret = method.invoke(LocalRegistry.getInstance().get(clazz), invoke.args);
      String retStr = String.valueOf(ret);

      // 创建http响应
      FullHttpResponse response = new DefaultFullHttpResponse(
          HttpVersion.HTTP_1_1,
          HttpResponseStatus.OK,
          Unpooled.copiedBuffer(retStr, CharsetUtil.UTF_8));
      // 设置头信息
      response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
      ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    } catch (Exception e) {
      respErr(ctx, "invoke err");
    }
  }

  private void respErr(ChannelHandlerContext ctx, String msg) {
    // 创建http响应
    FullHttpResponse response = new DefaultFullHttpResponse(
        HttpVersion.HTTP_1_1,
        HttpResponseStatus.OK,
        Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
    // 设置头信息
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }

  private TextInvoke parseInvoke(String uri, String src) {
    try {
      String clazz = uri.substring(1, uri.lastIndexOf("/"));
      String method = uri.substring(uri.lastIndexOf("/") + 1);

      String argBody = src.substring(1, src.length() - 1);
      String[] argstr = argBody.split(",");
      Object[] args = new Object[argstr.length];
      for (int i = 0; i < argstr.length; i++) {
        String arg = argstr[i].trim();
        if (arg.startsWith("\"")) {
          args[i] = arg.substring(1, arg.length() - 1);
        } else {
          throw new IllegalStateException();
        }
      }
      return new TextInvoke(clazz, method, args);
    } catch (Exception e) {
      return null;
    }
  }
}
