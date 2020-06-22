package com.mng.rpc.codec;

public class DubboResponse {

  public final Long id;
  public final Object result;

  public DubboResponse(Long id, Object result) {
    this.id = id;
    this.result = result;
  }
}
