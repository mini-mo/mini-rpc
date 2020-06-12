package com.mng.rpc;

import com.mng.rpc.protocol.dubbo.NettyServer;

public class ServerDubboMain {

  public static void main(String[] args) {

    ServerThread thread = new ServerThread();
    thread.start();
  }

  static class ServerThread extends Thread {

    NettyServer server;

    @Override
    public void run() {
      server = new NettyServer("192.168.160.55", 20888);
      server.doOpen();
    }
  }

}
