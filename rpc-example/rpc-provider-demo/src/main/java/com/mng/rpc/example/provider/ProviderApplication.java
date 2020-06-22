package com.mng.rpc.example.provider;

import com.mng.rpc.example.api.HelloService;
import com.mng.rpc.example.provider.service.HelloServiceImpl;
import com.mng.rpc.protocol.dubbo.NettyServer;
import com.mng.rpc.proxy.ProviderInvocationHandler;
import com.mng.rpc.proxy.ProxyFactory;
import com.mng.rpc.registry.LocalRegistry;

public class ProviderApplication {


  public static void main(String[] args) {
    HelloService helloService = new HelloServiceImpl();

    HelloService instance = ProxyFactory
        .newProxy(HelloService.class, new ProviderInvocationHandler(helloService));

    // bridge
    LocalRegistry.getInstance().register(HelloService.class, instance);

    ServerThread thread = new ServerThread();
    thread.start();
  }

  static class ServerThread extends Thread {

    NettyServer server;

    @Override
    public void run() {
      server = new NettyServer("0.0.0.0", 20888);
      server.doOpen();
    }
  }
}
