package com.mng.rpc.example.consumer;

import com.mng.rpc.client.NettyTmpClient;
import com.mng.rpc.example.api.HelloService;
import com.mng.rpc.proxy.ConsumerInvocationHandler;
import com.mng.rpc.proxy.ProxyFactory;
import java.util.Scanner;

public class Consumer3Application {

  public static void main(String[] args) throws Throwable {
    NettyTmpClient client = new NettyTmpClient("127.0.0.1", 20888);
    client.doOpen();
    client.doConnect();

    HelloService helloService = ProxyFactory.newProxy(HelloService.class, new ConsumerInvocationHandler(client));

    long id = 0L;
    Scanner scanner = new Scanner(System.in);
    while (true) {
      while (scanner.hasNextLine()) {
        scanner.nextLine();
        helloService.format2("test %s", id++ + "").thenAccept(System.out::println);
      }
    }
  }
}
