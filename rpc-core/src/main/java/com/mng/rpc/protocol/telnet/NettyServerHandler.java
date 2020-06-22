package com.mng.rpc.protocol.telnet;

import com.mng.rpc.registry.LocalRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Sharable
public class NettyServerHandler extends ChannelDuplexHandler {

  private NettyServer server;
  private Map<String, EndpointCtx> endpoints;

  public NettyServerHandler(NettyServer server) {
    this.server = server;

    endpoints = new ConcurrentHashMap<>();
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    SocketAddress sa = ctx.channel().remoteAddress();
    InetSocketAddress isa = (InetSocketAddress) sa;
    String key = isa.getHostString() + ":" + isa.getPort();
    endpoints.put(key, new EndpointCtx());
    super.channelActive(ctx);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    SocketAddress sa = ctx.channel().remoteAddress();
    InetSocketAddress isa = (InetSocketAddress) sa;
    String key = isa.getHostString() + ":" + isa.getPort();
    endpoints.remove(key);
    super.channelInactive(ctx);
  }

  @Override
  public void read(ChannelHandlerContext ctx) throws Exception {
    super.read(ctx);
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
      throws Exception {
    super.write(ctx, msg, promise);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof ByteBuf) {
      ByteBuf buf = (ByteBuf) msg;
      int len = buf.readableBytes();
      byte[] dst = new byte[len];
      buf.readBytes(dst, 0, len);
      String str = new String(dst);

      String cmd = str.trim();
      doCmd(ctx, cmd);
      ReferenceCountUtil.release(msg);
    } else {
      super.channelRead(ctx, msg);
    }
  }

  private void doCmd(ChannelHandlerContext ctx, String cmd) {
    if (cmd.equals("")) {
      ctx.writeAndFlush(Unpooled.wrappedBuffer("rpc>".getBytes()));
      return;
    }
    if (cmd.equals("history")) {
      Map<String, String> list = endpoints.get(genKey(ctx)).getInvokeHistory();
      StringBuilder sb = new StringBuilder();
      list.forEach((key, val) -> {
        sb.append(key).append("    ").append(val).append("\r\n");
      });
      sb.append("rpc>");
      ctx.writeAndFlush(Unpooled.wrappedBuffer(sb.toString().getBytes()));
      return;
    }
    if (cmd.startsWith("ls")) {
      List<Class> classes = LocalRegistry.getInstance().allKeys();
      StringBuilder sb = new StringBuilder();
      for (Class cls : classes) {
        sb.append(cls.getName()).append("\r\n");
        for (Method method : cls.getDeclaredMethods()) {
          sb.append("    ").append(method.getName()).append("\r\n");
        }
      }
      sb.append("rpc>");
      ctx.writeAndFlush(Unpooled.wrappedBuffer(sb.toString().getBytes()));
      return;
    }
    if (cmd.startsWith("invoke ")) {
      TextInvoke invoke = parseInvoke(cmd);
      if (invoke == null) {
        ctx.writeAndFlush(Unpooled.wrappedBuffer("not found valid provider\r\nrpc>".getBytes()));
        return;
      }

      Class clazz = LocalRegistry.getInstance().allKeys().stream()
          .filter(it -> Objects.equals(it.getName(), invoke.clazz))
          .findFirst().orElse(null);

      if (clazz == null) {
        ctx.writeAndFlush(Unpooled.wrappedBuffer("not found valid provider\r\nprc>".getBytes()));
        return;
      }

      Method method = Arrays.stream(clazz.getDeclaredMethods())
          .filter(it -> Objects.equals(it.getName(), invoke.method))
          .findFirst().orElse(null);
      if (method == null) {
        ctx.writeAndFlush(Unpooled.wrappedBuffer("not found valid provider\r\nrpc>".getBytes()));
        return;
      }

      try {
        Object ret = method.invoke(LocalRegistry.getInstance().get(clazz), invoke.args);
        String retStr = String.valueOf(ret);

        EndpointCtx endpointCtx = endpoints.get(genKey(ctx));
        if (endpointCtx == null) {
          throw new IllegalStateException();
        }
        endpointCtx.getInvokeHistory().put(cmd, retStr);

        ctx.writeAndFlush(Unpooled.wrappedBuffer((retStr + "\r\nrpc>").getBytes()));
        return;
      } catch (Exception e) {
        ctx.writeAndFlush(Unpooled.wrappedBuffer("invoke target err\r\nrpc>".getBytes()));
        return;
      }
    }
  }

  // invoke com.mng.rpc.server.HelloService.format("Hello %s", "Mini-rpc")
  // clazz com.mng.rpc.server.HelloService
  // method format
  // args String[]{"Hello %s","Mini-rpc"}
  private TextInvoke parseInvoke(String cmd) {
    try {
      String body = cmd.substring("invoke ".length()).trim();
      if (body.length() == 0) {
        return null;
      }

      int abi = body.indexOf("(");
      if (abi < 0) {
        return null;
      }
      int aei = body.indexOf(")");
      if (aei < 0) {
        return null;
      }
      if (abi >= aei) {
        return null;
      }

      String argBody = body.substring(abi + 1, aei);
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

      String cmBody = body.substring(0, abi);
      int mbi = cmBody.lastIndexOf(".");
      if (mbi < 0) {
        return null;
      }

      String clazz = cmBody.substring(0, mbi);
      String method = cmBody.substring(mbi + 1);
      return new TextInvoke(clazz, method, args);
    } catch (Exception e) {
      return null;
    }
  }
// invoke com.mng.rpc.server.HelloService.format("Hello %s", "Mini-rpc")
//  public static void main(String[] args) {
//    NettyServerHandler handler = new NettyServerHandler(null);
//    handler
//        .parseInvoke("invoke com.mng.rpc.server.HelloService.format(\"Hello %s\", \"Mini-rpc\")");
//  }

  private String genKey(ChannelHandlerContext ctx) {
    SocketAddress sa = ctx.channel().remoteAddress();
    InetSocketAddress isa = (InetSocketAddress) sa;
    String key = isa.getHostString() + ":" + isa.getPort();
    return key;
  }
}
