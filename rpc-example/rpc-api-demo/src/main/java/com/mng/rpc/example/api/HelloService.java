package com.mng.rpc.example.api;

import java.util.concurrent.CompletableFuture;

public interface HelloService {

  String hello(String msg);

  String format(String format, String name);

  String add(String format, int x, int y);

  CompletableFuture<String> format2(String format, String name);
}
