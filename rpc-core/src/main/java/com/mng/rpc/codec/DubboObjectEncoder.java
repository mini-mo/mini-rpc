package com.mng.rpc.codec;

import com.mng.rpc.playload.DubboMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

public class DubboObjectEncoder<H extends DubboMessage> extends MessageToMessageEncoder<Object> {

  @Override
  protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {

  }
}
