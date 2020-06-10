package com.mng.rpc.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;

@Sharable
public class NettyClientTmpHandler extends ChannelDuplexHandler {

  private NettyTmpClient client;

  public NettyClientTmpHandler(NettyTmpClient client) {
    this.client = client;
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
      System.out.print(str);
      ReferenceCountUtil.release(msg);
    } else {
      super.channelRead(ctx, msg);
    }
  }
}
