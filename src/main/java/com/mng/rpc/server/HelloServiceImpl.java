package com.mng.rpc.server;

public class HelloServiceImpl implements HelloService {

  @Override
  public String hello(String msg) {
    return "form provider " + msg;
  }

  @Override
  public String format(String format, String name) {
    return String.format(format, name);
  }
}
