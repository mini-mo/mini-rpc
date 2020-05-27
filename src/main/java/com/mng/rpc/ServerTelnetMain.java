package com.mng.rpc;

import com.mng.rpc.protocol.telnet.NettyServer;

public class ServerTelnetMain {

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
