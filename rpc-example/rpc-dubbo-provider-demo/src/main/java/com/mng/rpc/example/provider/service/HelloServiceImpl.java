package com.mng.rpc.example.provider.service;

import com.mng.rpc.example.api.HelloService;

public class HelloServiceImpl implements HelloService {

  @Override
  public String hello(String msg) {
    return "from provider " + msg;
  }

  @Override
  public String format(String format, String name) {
    return String.format(format, name);
  }

}
