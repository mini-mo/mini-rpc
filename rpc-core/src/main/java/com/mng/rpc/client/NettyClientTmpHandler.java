package com.mng.rpc.client;

import com.mng.rpc.codec.DubboResponse;
import com.mng.rpc.consumer.CTX;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

@Sharable
public class NettyClientTmpHandler extends SimpleChannelInboundHandler<DubboResponse> {

  private NettyTmpClient client;

  public NettyClientTmpHandler(NettyTmpClient client) {
    this.client = client;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, DubboResponse msg) throws Exception {
    CTX.release(msg);
  }
}
