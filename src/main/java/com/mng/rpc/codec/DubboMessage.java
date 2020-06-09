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
    return header[length - 4] << 24
        | header[length - 3] << 16
        | header[length - 2] << 8
        | header[length - 1];
  }
}
