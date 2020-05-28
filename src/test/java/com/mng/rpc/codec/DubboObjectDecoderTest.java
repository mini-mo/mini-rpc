package com.mng.rpc.codec;

import io.netty.buffer.Unpooled;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class DubboObjectDecoderTest {

  DubboObjectDecoder objectDecoder = new DubboObjectDecoder();

  @Test
  @Ignore
  public void decode() throws Exception {

    byte[] bytes = new byte[]{
        1,
        2,
        3,
        4,
        5,
        6,
        7,
        8,
        9,
        10,
        11,
        12,
        13,
        14,
        15,
        16,
        17,
    };
    ArrayList<Object> list = new ArrayList<>();
    objectDecoder.decode(null, Unpooled.copiedBuffer(bytes), list);
  }

  @Test
  @Ignore
  public void decode2() throws Exception {
//    byte[] bytes = new byte[]{
//        1,
//        2,
//        3,
//        4,
//        5,
//        6,
//        7,
//        8,
//        9,
//        10,
//        11,
//        12,
//        13,
//        14,
//        15,
//    };

    byte[] clazzBytes = "com.mng.rpc.server.HelloService".getBytes();
    byte[] methodBytes = "format".getBytes();
    byte[] argsBytes = "\"Hello %s\",\"Mini-rpc\"".getBytes();
    int len = 4 + clazzBytes.length + 4 + methodBytes.length + 4 + argsBytes.length;

    ByteArrayOutputStream bao = new ByteArrayOutputStream(16 + len);
    DataOutputStream dos = new DataOutputStream(bao);
    dos.writeByte(0xda); // magic high  8 bit
    dos.writeByte(0xbb); // magic low 8 bit
    dos.writeByte(0b1100001); // req/resp 1bit, 2 way 1bit, event 1 bit, serialization id 5 bit,
    dos.writeByte(1); // status 8 bit
    dos.writeLong(1); // request id 64 bit
    dos.writeInt(len); // data length 32 bit

    dos.writeInt(clazzBytes.length);
    dos.writeBytes(new String(clazzBytes));
    dos.writeInt(methodBytes.length);
    dos.writeBytes(new String(methodBytes));
    dos.writeInt(argsBytes.length);
//    dos.writeBytes(new String(argsBytes));
    dos.flush();
    byte[] bytes = bao.toByteArray();

    ArrayList<Object> list = new ArrayList<>();
    objectDecoder.decode(null, Unpooled.copiedBuffer(bytes), list);

    objectDecoder.decode(null, Unpooled.copiedBuffer(argsBytes), list);

    objectDecoder.decode(null, Unpooled.copiedBuffer(bytes), list);
    objectDecoder.decode(null, Unpooled.copiedBuffer(argsBytes), list);

    Assert.assertEquals(2, list.size());
  }
}