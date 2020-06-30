package com.mng.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ByteProcessor;
import java.util.List;

public class DubboObjectDecoder extends ByteToMessageDecoder {

  private State currentState = State.READ_HEADER;
  private HeaderParser headerParser;
  private DubboMessage dubboMessage = new DubboMessage();
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
    decode0(ctx, buffer, out);
  }

  private void decode0(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
    switch (currentState) {
      case READ_HEADER:
        State nextState = readHeader(buffer);
        if (nextState == null) {
          return;
        }
        currentState = nextState;
        switch (nextState) {
          case READ_FIXED_LENGTH_CONTENT:
            int readableBytes = buffer.readableBytes();
            if (readableBytes <= 0) {
              return;
            }

            int dataLen = this.dubboMessage.getDataLen();

            // 未读过
            if (contentLen == -1) {
              // 刚好整包
              if (dataLen == readableBytes) {
                byte[] bytes = new byte[dataLen];
                buffer.readBytes(bytes, 0, dataLen);
                this.dubboMessage.setBody(bytes);
                out.add(this.dubboMessage);
                reset();
                return;
              }

              // 不足
              if (dataLen > readableBytes) {
                byte[] bytes = new byte[dataLen];
                buffer.readBytes(bytes, 0, readableBytes);
                this.dubboMessage.setBody(bytes);
                contentLen = readableBytes;
                return;
              }

              // 大于一个包
              byte[] bytes = new byte[dataLen];
              buffer.readBytes(bytes, 0, dataLen);
              this.dubboMessage.setBody(bytes);
              out.add(this.dubboMessage);
              reset();
              // 继续解析
              decode0(ctx, buffer, out);
              return;
            }
        }
        throw new IllegalStateException();
      case READ_FIXED_LENGTH_CONTENT:
        int readableBytes = buffer.readableBytes();
        if (readableBytes <= 0) {
          return;
        }

        int dataLen = this.dubboMessage.getDataLen();

        // 未读过
        if (contentLen == -1) {
          // 刚好整包
          if (dataLen == readableBytes) {
            byte[] bytes = new byte[dataLen];
            buffer.readBytes(bytes, 0, dataLen);
            this.dubboMessage.setBody(bytes);
            out.add(this.dubboMessage);
            reset();
            return;
          }

          // 不足
          if (dataLen < readableBytes) {
            byte[] bytes = new byte[dataLen];
            buffer.readBytes(bytes, 0, readableBytes);
            this.dubboMessage.setBody(bytes);
            contentLen = readableBytes;
            return;
          }

          // 大于一个包
          byte[] bytes = new byte[dataLen];
          buffer.readBytes(bytes, 0, dataLen);
          this.dubboMessage.setBody(bytes);
          out.add(this.dubboMessage);
          reset();
          // 继续解析
          decode0(ctx, buffer, out);
          return;
        }

        byte[] body = this.dubboMessage.getBody();
        if (body == null) {
          throw new IllegalStateException();
        }

        int need = dataLen - contentLen;
        if (need == readableBytes) {
          buffer.readBytes(body, contentLen, need);
          out.add(this.dubboMessage);
          reset();
          return;
        }
        if (need > readableBytes) {
          buffer.readBytes(body, contentLen, readableBytes);
          contentLen += need;
          return;
        }
        buffer.readBytes(body, contentLen, need);
        out.add(this.dubboMessage);
        reset();
        // 继续解析
        decode0(ctx, buffer, out);
        return;
    }
  }

  private State readHeader(ByteBuf buffer) {
    byte[] bytes = headerParser.parse(buffer);
    if (bytes == null) {
      return null;
    }
    this.dubboMessage.setHeader(bytes);
    return State.READ_FIXED_LENGTH_CONTENT;
  }

  private void reset() {
    headerParser.reset();
    contentLen = -1;
    currentState = State.READ_HEADER;
    dubboMessage = new DubboMessage();
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
