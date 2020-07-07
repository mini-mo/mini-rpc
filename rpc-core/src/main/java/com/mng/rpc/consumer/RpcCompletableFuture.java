package com.mng.rpc.consumer;

import com.mng.rpc.codec.DubboRequest;
import java.util.concurrent.CompletableFuture;

public class RpcCompletableFuture<Object> extends CompletableFuture<Object> {

  private final DubboRequest dubboRequest;

  public RpcCompletableFuture(DubboRequest dubboRequest) {
    this.dubboRequest = dubboRequest;
  }

  public DubboRequest getRequest() {
    return dubboRequest;
  }
}
