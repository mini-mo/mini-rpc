package com.mng.rpc.codec;

public class DubboMessage {

  private byte[] header;
  private byte[] body;

  public byte[] getHeader() {
    return header;
  }

  public void setHeader(byte[] header) {
    this.header = header;
  }

  public byte[] getBody() {
    return body;
  }

  public void setBody(byte[] body) {
    this.body = body;
  }

  public int getDataLen() {
    // header 最后四个 byte
    int length = header.length;
    if (length < 4) {
      throw new IllegalStateException();
    }
    return bytes2int(header, 12);
  }

  public static int bytes2int(byte[] b, int off) {
    return ((b[off + 3] & 0xFF) << 0) +
        ((b[off + 2] & 0xFF) << 8) +
        ((b[off + 1] & 0xFF) << 16) +
        ((b[off + 0]) << 24);
  }
}
