package com.mng.rpc.server;

import com.mng.rpc.Main;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

@Sharable
public class NettyServerHandler extends ChannelDuplexHandler {

  private NettyServer server;

  public NettyServerHandler(NettyServer server) {
    this.server = server;
    HelloService helloService = new HelloServiceImpl();

    Object instance = Proxy.newProxyInstance(Main.class.getClassLoader(), new Class[]{HelloService.class},
        new ProviderInvocationHandler(helloService));
    // bridge
    LocalRegistry.getInstance().register(HelloService.class, instance);
  }

  @Override
  public void read(ChannelHandlerContext ctx) throws Exception {
    super.read(ctx);
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
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
    if (cmd.startsWith("ls")) {
      List<Class> classes = LocalRegistry.getInstance().allKeys();
      StringBuilder sb = new StringBuilder();
      for (Class cls : classes) {
        sb.append(cls.getSimpleName()).append("\r\n");
        for (Method method : cls.getDeclaredMethods()) {
          sb.append("    ").append(method.getName()).append("\r\n");
        }
      }
      sb.append("dubbo>");
      ctx.writeAndFlush(Unpooled.wrappedBuffer(sb.toString().getBytes()));
    }
  }
}
