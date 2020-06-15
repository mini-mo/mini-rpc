package com.mng.rpc.consumer;

import com.mng.rpc.client.NettyTmpClient;
import com.mng.rpc.server.HelloService;
import java.lang.reflect.Proxy;

public class TestConsumer {

  public static void main(String[] args) throws Throwable {
    NettyTmpClient client = new NettyTmpClient("192.168.160.55", 20888);
    client.doOpen();
    client.doConnect();
    HelloService helloService = (HelloService) Proxy
        .newProxyInstance(TestConsumer.class.getClassLoader(), new Class[]{HelloService.class},
            new ConsumerInvocationHandler(client));

    String test = helloService.hello("mini-rpc");
    System.out.println(test);
  }
}
