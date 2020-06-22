package com.mng.rpc.example.provider;

import com.mng.rpc.example.api.HelloService;
import com.mng.rpc.example.provider.service.HelloServiceImpl;
import java.io.IOException;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;

public class DubboProviderApplication {

  public static void main(String[] args) throws IOException {
    ServerThread thread = new ServerThread();
    thread.start();

    System.in.read();
  }

  static class ServerThread extends Thread {

    @Override
    public void run() {
      ServiceConfig<HelloServiceImpl> service = new ServiceConfig<>();

      ApplicationConfig application = new ApplicationConfig("dubbo-demo-api-provider");
      application.setQosEnable(false);
      service.setApplication(application);

      RegistryConfig registry = new RegistryConfig("N/A");
      registry.setCheck(false);
      service.setRegistry(registry);

      service.setProtocol(new ProtocolConfig());
      service.setInterface(HelloService.class);
      service.setRef(new HelloServiceImpl());
      service.export();
    }
  }
}
