package com.mng.rpc.server;

public interface HelloService {

  void hello(String msg);

  String format(String format, String name);
}
