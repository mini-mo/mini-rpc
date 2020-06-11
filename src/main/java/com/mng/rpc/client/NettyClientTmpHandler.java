package com.mng.rpc.client;

import com.mng.rpc.codec.DubboResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

@Sharable
public class NettyClientTmpHandler extends SimpleChannelInboundHandler<DubboResponse> {

  private NettyTmpClient client;

  public NettyClientTmpHandler(NettyTmpClient client) {
    this.client = client;
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

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, DubboResponse msg) throws Exception {
    System.out.println(msg.result);
  }
}
