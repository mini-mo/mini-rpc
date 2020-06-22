package com.mng.rpc.example.consumer;

import com.mng.rpc.client.NettyTmpClient;
import com.mng.rpc.example.api.HelloService;
import com.mng.rpc.proxy.ConsumerInvocationHandler;
import com.mng.rpc.proxy.ProxyFactory;
import java.util.Scanner;

public class ConsumerApplication {

  public static void main(String[] args) throws Throwable {
    NettyTmpClient client = new NettyTmpClient("127.0.0.1", 20888);
    client.doOpen();
    client.doConnect();

    HelloService helloService = ProxyFactory.newProxy(HelloService.class, new ConsumerInvocationHandler(client));

    Scanner scanner = new Scanner(System.in);
    while (true) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if (line.isEmpty()) {
          line = "mini-rpc";
        }
        String test = helloService.format("ret %s", line);
        System.out.println(test);
      }
    }
  }
}