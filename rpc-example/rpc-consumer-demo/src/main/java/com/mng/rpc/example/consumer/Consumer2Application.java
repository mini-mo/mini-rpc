package com.mng.rpc.example.consumer;

import com.mng.rpc.client.NettyTmpClient;
import com.mng.rpc.example.api.AccountDTO;
import com.mng.rpc.example.api.AccountService;
import com.mng.rpc.proxy.ConsumerInvocationHandler;
import com.mng.rpc.proxy.ProxyFactory;
import java.util.Scanner;

public class Consumer2Application {

  public static void main(String[] args) throws Throwable {
    NettyTmpClient client = new NettyTmpClient("127.0.0.1", 20888);
    client.doOpen();
    client.doConnect();

    AccountService accountService = ProxyFactory.newProxy(AccountService.class, new ConsumerInvocationHandler(client));

    long id = 0L;
    Scanner scanner = new Scanner(System.in);
    while (true) {
      while (scanner.hasNextLine()) {
        scanner.nextLine();
        AccountDTO account = accountService.findAccountById(id++);
        System.out.println(account);
      }
    }
  }
}
