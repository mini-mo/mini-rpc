package com.mng.rpc.codec;

import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import io.netty.buffer.Unpooled;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;

public class DubboObjectDecoderTest {

  DubboObjectDecoder objectDecoder = new DubboObjectDecoder();

  @Test
  public void decode2() throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    Hessian2Output output = new Hessian2Output(baos);

    output.writeString("2.0.2");
    output.writeString("com.gxk.demo.service.HelloService");
    output.writeString("0.0.0");
    output.writeString("hello");
    output.writeString("Ljava/lang/String;");
    // args
    output.writeString("dubbo");
    output.flush();

    byte[] bodyBytes = baos.toByteArray();
    int len = bodyBytes.length;

    ByteArrayOutputStream bao = new ByteArrayOutputStream(16);
    DataOutputStream dos = new DataOutputStream(bao);
    dos.writeByte(0xda); // magic high  8 bit
    dos.writeByte(0xbb); // magic low 8 bit
    dos.writeByte(0b1100001); // req/resp 1bit, 2 way 1bit, event 1 bit, serialization id 5 bit,
    dos.writeByte(1); // status 8 bit
    dos.writeLong(1); // request id 64 bit
    dos.writeInt(len); // data length 32 bit
    dos.flush();
    byte[] bytes = bao.toByteArray();

    ArrayList<Object> list = new ArrayList<>();
    // 1
    objectDecoder.decode(null, Unpooled.copiedBuffer(bytes), list);
    objectDecoder.decode(null, Unpooled.copiedBuffer(bodyBytes), list);

    Assert.assertEquals(1, list.size());
  }
}