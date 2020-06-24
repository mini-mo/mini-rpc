package com.mng.rpc.example.provider.service;

import com.mng.rpc.example.api.AccountDTO;
import com.mng.rpc.example.api.AccountService;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class AccountServiceImpl implements AccountService {

  private AtomicLong id = new AtomicLong();

  @Override
  public Long createAccount(String username) {
    return id.incrementAndGet();
  }

  @Override
  public AccountDTO findAccountById(Long id) {
    AccountDTO dto = new AccountDTO();
    dto.setId(id);
    dto.setCreateAt(new Date());
    dto.setUsername(id + " " + id);
    return dto;
  }
}
