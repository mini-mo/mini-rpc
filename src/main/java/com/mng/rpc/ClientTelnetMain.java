package com.mng.rpc;

import com.mng.rpc.client.NettyClient;
import io.netty.buffer.Unpooled;
import java.util.Scanner;

public class ClientTelnetMain {

  public static void main(String[] args) throws Throwable {
    ClientThread thread = new ClientThread();
    thread.start();

    Scanner scanner = new Scanner(System.in);
    while (true) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        line += "\r\n";
        thread.client.send(Unpooled.wrappedBuffer(line.getBytes()));
      }
    }
  }

  static class ClientThread extends Thread {

    NettyClient client = null;

    @Override
    public void run() {
      try {
        client = new NettyClient("127.0.0.1", 20880);
        client.doOpen();
        client.doConnect();
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }
}
