package com.mng.rpc.codec;

public class DubboRequest {

  private String msg;

  public DubboRequest(String msg) {
    this.msg = msg;
  }

  public String getMsg() {
    return msg;
  }
}
