package com.mng.rpc.example.provider.service;

import com.mng.rpc.example.api.HelloService;
import java.util.concurrent.CompletableFuture;

public class HelloServiceImpl implements HelloService {

  @Override
  public String hello(String msg) {
    return "from provider " + msg;
  }

  @Override
  public String format(String format, String name) {
    return String.format(format, name);
  }

  @Override
  public String add(String format, int x, int y) {
    return String.format(format, x + y);
  }

  @Override
  public CompletableFuture<String> format2(String format, String name) {
    return CompletableFuture.supplyAsync(() -> this.format(format, name));
  }
}
