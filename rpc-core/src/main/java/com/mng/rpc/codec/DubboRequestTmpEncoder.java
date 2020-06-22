package com.mng.rpc.codec;

import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DubboRequestTmpEncoder extends MessageToByteEncoder<DubboRequest> {

  private static final Logger logger = LoggerFactory.getLogger(DubboRequestTmpEncoder.class);

  @Override
  protected void encode(ChannelHandlerContext ctx, DubboRequest request, ByteBuf out) throws Exception {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Hessian2Output output = new Hessian2Output(baos);

    String path = Optional.ofNullable(request.getPath()).orElse("com.gxk.demo.service.HelloService");
    String method = Optional.ofNullable(request.getMethod()).orElse("hello");
    output.writeString("2.0.2");
    output.writeString(path);
    output.writeString("0.0.0");
    output.writeString(method);
    output.writeString(request.getMethodParameterDescriptor());
    // args
    for (Object arg : request.getArgs()) {
      output.writeObject(arg);
    }
    HashMap<String, String> map = new HashMap<>();
    output.writeObject(map);
    output.flush();

    byte[] bodyBytes = baos.toByteArray();
    int len = bodyBytes.length;

    ByteArrayOutputStream bao = new ByteArrayOutputStream(16);
    DataOutputStream dos = new DataOutputStream(bao);
    dos.writeByte(0xda); // magic high  8 bit
    dos.writeByte(0xbb); // magic low 8 bit
    dos.writeByte(0b11000010); // req/resp 1bit, 2 way 1bit, event 1 bit, serialization id 5 bit,
    dos.writeByte(1); // status 8 bit
    dos.writeLong(request.getId()); // request id 64 bit
    dos.writeInt(len); // data length 32 bit
    dos.flush();
    byte[] bytes = bao.toByteArray();

    int begin = out.writerIndex();
    out.writeBytes(bytes);
    out.writeBytes(bodyBytes);
    int end = out.writerIndex();

//    logger.info("request encoder write bytes {}", end - begin);
  }
}
