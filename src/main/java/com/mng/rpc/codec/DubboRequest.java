package com.mng.rpc.codec;

public class DubboRequest {

  private String path;
  private String method;
  private String msg;

  // com.xxx.Service.hello(Ljava/lang/String;)#0.0.0
  public DubboRequest(String path, String method, String msg) {
    this.path = path;
    this.method = method;
    this.msg = msg;
  }

  public String getMsg() {
    return msg;
  }

  public String getPath() {
    return path;
  }

  public String getMethod() {
    return method;
  }
}
