package com.mng.rpc.example.api;

public interface AccountService {

  Long createAccount(String username);

  AccountDTO findAccountById(Long id);
}
