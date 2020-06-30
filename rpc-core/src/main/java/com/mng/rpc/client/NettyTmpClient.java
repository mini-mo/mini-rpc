package com.mng.rpc.client;

import com.mng.rpc.codec.DubboRequestTmpEncoder;
import com.mng.rpc.codec.DubboResponseTmpDecoder;
import com.mng.rpc.consumer.CTX;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.net.InetSocketAddress;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

public class NettyTmpClient {

  private final String host;
  private final Integer port;

  private Bootstrap bootstrap;
  private Channel channel;
  private NioEventLoopGroup group;

  public NettyTmpClient(String host, Integer port) {
    this.host = host;
    this.port = port;
  }

  public void doOpen() throws Throwable {
    final NettyClientTmpHandler nettyClientHandler = new NettyClientTmpHandler(this);
    bootstrap = new Bootstrap();
    group = new NioEventLoopGroup(4);
    bootstrap.group(group)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .option(ChannelOption.TCP_NODELAY, true)
        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
        .channel(NioSocketChannel.class);

    bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
    bootstrap.handler(new ChannelInitializer<SocketChannel>() {
      @Override
      protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline()
//            .addLast("logging", new LoggingHandler(LogLevel.INFO))//for debug
            .addLast("encoder", new DubboRequestTmpEncoder())
            .addLast("decoder", new DubboResponseTmpDecoder())
//            .addLast("aggregator", new DubboObjectAggregator(Integer.MAX_VALUE))
            .addLast("handler", nettyClientHandler)
        ;
      }
    });
  }

  public void doConnect() throws Throwable {
    long start = System.currentTimeMillis();
    ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));
    try {
      boolean ret = future.awaitUninterruptibly(2000, TimeUnit.MILLISECONDS);
      if (ret && future.isSuccess()) {
        channel = future.channel();
//        System.out.println(channel);
      } else if (future.cause() != null) {
        future.cause().printStackTrace();
        throw new RemoteException();
      } else {
        throw new RemoteException();
      }
    } finally {
      long end = System.currentTimeMillis();
//      System.out.println("connect " + (end - start));
      // noop
    }
  }

  public void send(Object msg) {

    ChannelFuture future = channel.writeAndFlush(msg);
    boolean ret = future.awaitUninterruptibly(5000, TimeUnit.MILLISECONDS);
    if (ret && future.isSuccess()) {
//      System.out.println("1");
    }

    if (future.cause() != null) {
      future.cause().printStackTrace();
    }
//    System.out.println(future);
  }

  public void shutdown() {
    group.shutdownGracefully();
    CTX.shutdown();
  }
}
