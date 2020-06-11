package com.mng.rpc.codec;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DubboResponseTmpDecoder extends ByteToMessageDecoder {

  private DubboObjectDecoder decoder;

  public DubboResponseTmpDecoder() {
    this.decoder = new DubboObjectDecoder();
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    this.decoder.decode(ctx, in, out);
    if (out.isEmpty()) {
      return;
    }

    Iterator<Object> iterator = out.iterator();
    while (iterator.hasNext()) {
      Object msg = iterator.next();
      if (msg instanceof DubboMessage) {
        DubboMessage dubboMessage = (DubboMessage) msg;

        ByteArrayInputStream bais = new ByteArrayInputStream(dubboMessage.getBody());
        Hessian2Input input = new Hessian2Input(bais);

        byte flag = (byte) input.readInt();
        Object resp = input.readObject(String.class);
        Object ext = input.readObject(Map.class);

        out.add(new DubboResponse(resp));
      }
    }
  }
}
