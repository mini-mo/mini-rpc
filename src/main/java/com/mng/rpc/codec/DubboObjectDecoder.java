package com.mng.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ByteProcessor;
import java.util.List;

public class DubboObjectDecoder extends ByteToMessageDecoder {

  private State currentState = State.READ_HEADER;
  private HeaderParser headerParser;
  private int contentLen = -1;

  public DubboObjectDecoder() {
    headerParser = new HeaderParser(new byte[16]);
  }

  private enum State {
    READ_HEADER,
    READ_FIXED_LENGTH_CONTENT,
    BAD_MESSAGE,
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out)
      throws Exception {
    switch (currentState) {
      case READ_HEADER:
        State nextState = readHeader(buffer);
        if (nextState == null) {
          return;
        }
        throw new IllegalStateException();
      case READ_FIXED_LENGTH_CONTENT:
        int readLimit = buffer.readableBytes();

    }
  }

  private State readHeader(ByteBuf buffer) {
    byte[] bytes = headerParser.parse(buffer);
    if (bytes == null) {
      return null;
    }
    return State.READ_FIXED_LENGTH_CONTENT;
  }

  private void reset() {
    headerParser.reset();
    contentLen = -1;
    currentState = State.READ_HEADER;
  }

  private static class HeaderParser implements ByteProcessor {

    private final byte[] seq;
    private int size;

    HeaderParser(byte[] seq) {
      this.seq = seq;
    }

    public byte[] parse(ByteBuf buffer) {
      final int oldSize = size;
      int i = buffer.forEachByte(this);
      if (i == -1) {
        size = oldSize;
        return null;
      }
      buffer.readerIndex(i + 1);
      if (size + 1 != seq.length) {
        return null;
      }
      return seq;
    }

    public void reset() {
      size = 0;
    }

    @Override
    public boolean process(byte value) {
      seq[size] = value;
      if (size + 1 == seq.length) {
        return false;
      }
      size++;
      return true;
    }
  }
}
