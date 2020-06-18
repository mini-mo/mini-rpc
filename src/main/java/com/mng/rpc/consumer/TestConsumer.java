package com.mng.rpc.consumer;

import com.mng.rpc.client.NettyTmpClient;
import com.mng.rpc.codec.DubboRequest;
import com.mng.rpc.server.HelloService;
import java.lang.reflect.Proxy;
import java.util.Scanner;

public class TestConsumer {

  public static void main(String[] args) throws Throwable {
    NettyTmpClient client = new NettyTmpClient("127.0.0.1", 20888);
    client.doOpen();
    client.doConnect();
    HelloService helloService = (HelloService) Proxy
        .newProxyInstance(TestConsumer.class.getClassLoader(), new Class[]{HelloService.class},
            new ConsumerInvocationHandler(client));

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
