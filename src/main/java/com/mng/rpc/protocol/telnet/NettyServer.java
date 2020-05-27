package com.mng.rpc.protocol.telnet;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServer {

  private final String host;
  private final Integer port;

  private ServerBootstrap bootstrap;
  private Channel channel;

  public NettyServer(String host, Integer port) {
    this.host = host;
    this.port = port;
  }

  public void doOpen() {
    bootstrap = new ServerBootstrap();

    bootstrap.group(new NioEventLoopGroup(1), new NioEventLoopGroup(4));
    bootstrap.channel(NioServerSocketChannel.class);

    bootstrap.childHandler(new ChannelInitializer() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline
            .addLast(new LoggingHandler(LogLevel.INFO))
            .addLast(new NettyServerHandler(NettyServer.this))
        ;
      }
    });

    ChannelFuture future = bootstrap.bind(host, port);

    future.syncUninterruptibly();
    channel = future.channel();
  }

}
