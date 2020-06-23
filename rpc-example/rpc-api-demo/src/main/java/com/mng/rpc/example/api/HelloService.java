package com.mng.rpc.example.api;

public interface HelloService {

  String hello(String msg);

  String format(String format, String name);

  String add(String format, int x, int y);
}
