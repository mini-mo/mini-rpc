package com.mng.rpc.protocol.telnet;

public class TextInvoke {

  public final String clazz;
  public final String method;
  public final Object[] args;

  public TextInvoke(String clazz, String method, Object[] args) {
    this.clazz = clazz;
    this.method = method;
    this.args = args;
  }
}
