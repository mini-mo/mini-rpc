package com.mng.rpc.codec;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import com.mng.rpc.util.Utils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DubboRequestTmpDecoder extends ByteToMessageDecoder {

  private DubboObjectDecoder decoder;

  public DubboRequestTmpDecoder() {
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
        input.readString();
        String path = input.readString();
        input.readString();
        String method = input.readString();
        String descriptor = input.readString();
        Class<?>[] pts = Utils.desc2classArray(descriptor);
        Object[] args = new Object[pts.length];
        for (int i = 0; i < pts.length; i++) {
          args[i] = input.readObject(pts[i]);
        }
        input.readObject(Map.class);

        Long id = dubboMessage.getId();
        out.add(DubboRequest.decode(id, path, method, descriptor, args));
      }
    }
  }
}
