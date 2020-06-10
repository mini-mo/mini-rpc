package com.mng.rpc;

import com.mng.rpc.client.NettyTmpClient;
import com.mng.rpc.codec.DubboRequest;
import java.util.Scanner;

public class ClientTmpMain {

  public static void main(String[] args) throws Throwable {
    ClientThread thread = new ClientThread();
    thread.start();

    Scanner scanner = new Scanner(System.in);
    while (true) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        line += "\r\n";
        System.out.println(line);
        thread.client.send(new DubboRequest());
      }
    }
  }

  static class ClientThread extends Thread {

    NettyTmpClient client = null;

    @Override
    public void run() {
      try {
        client = new NettyTmpClient("127.0.0.1", 20880);
        client.doOpen();
        client.doConnect();
      } catch (Throwable e) {
        e.printStackTrace();
      }
    }
  }
}
