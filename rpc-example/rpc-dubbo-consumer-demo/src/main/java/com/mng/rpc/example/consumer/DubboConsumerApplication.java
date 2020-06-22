package com.mng.rpc.example.consumer;

import com.mng.rpc.example.api.HelloService;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;

public class DubboConsumerApplication {

  public static void main(String[] args) throws Throwable {
    ReferenceConfig<HelloService> reference = new ReferenceConfig<>();
    ApplicationConfig application = new ApplicationConfig("dubbo-demo-api-consumer");
    application.setQosEnable(false);

    reference.setApplication(application);
    reference.setInterface(HelloService.class);
    reference.setUrl("dubbo://127.0.0.1:20880");
    HelloService service = reference.get();
    String message = service.hello("dubbo");
    System.out.println(message);
  }
}
