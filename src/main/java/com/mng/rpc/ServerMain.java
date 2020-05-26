package com.mng.rpc;

import com.mng.rpc.server.NettyServer;

public class ServerMain {

  public static void main(String[] args) {

    ServerThread thread = new ServerThread();
    thread.start();
  }

  static class ServerThread extends Thread {

    NettyServer server;

    @Override
    public void run() {
      server = new NettyServer("127.0.0.1", 20880);
      server.doOpen();
    }
  }

}
