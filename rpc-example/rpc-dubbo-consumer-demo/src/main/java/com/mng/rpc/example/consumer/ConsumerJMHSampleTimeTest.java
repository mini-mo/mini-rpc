package com.mng.rpc.example.consumer;

import com.mng.rpc.client.NettyTmpClient;
import com.mng.rpc.example.api.HelloService;
import com.mng.rpc.proxy.ConsumerInvocationHandler;
import com.mng.rpc.proxy.ProxyFactory;
import java.util.concurrent.TimeUnit;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class ConsumerJMHSampleTimeTest {

  @State(Scope.Benchmark)
  public static class MyState {

    HelloService s1;
    HelloService s2;
    NettyTmpClient client;

    @Setup
    public void setup() {
      ReferenceConfig<HelloService> reference = new ReferenceConfig<>();
      ApplicationConfig application = new ApplicationConfig("dubbo-demo-api-consumer");
      application.setQosEnable(false);

      reference.setApplication(application);
      reference.setInterface(HelloService.class);
      reference.setUrl("dubbo://127.0.0.1:20880");
      s1 = reference.get();

      try {
        client = new NettyTmpClient("127.0.0.1", 20888);
        client.doOpen();
        client.doConnect();
        s2 = ProxyFactory.newProxy(HelloService.class, new ConsumerInvocationHandler(client));
      } catch (Throwable e) {
        throw new IllegalStateException();
      }
    }

    @TearDown
    public void tearDown() {
      client.shutdown();
    }
  }

  @Benchmark
  public void testDubboHello(MyState state) {
    for (int i = 0; i < 100; i++) {
      String msg = "dubbo" + i;
      String message = state.s1.hello(msg);
    }
  }

  @Benchmark
  public void testDubboFormat(MyState state) {
    for (int i = 0; i < 100; i++) {
      String msg = "dubbo" + i;
      String message = state.s1.format("hhh %s", msg);
    }
  }

  @Benchmark
  public void testRpcHello(MyState state) {
    for (int i = 0; i < 100; i++) {
      String msg = "dubbo" + i;
      String message = state.s2.hello(msg);
    }
  }

  @Benchmark
  public void testRpcFormat(MyState state) {
    for (int i = 0; i < 100; i++) {
      String msg = "dubbo" + i;
      String message = state.s2.format("hhh %s", msg);
    }
  }
}
