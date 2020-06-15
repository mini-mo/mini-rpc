package com.mng.rpc.codec;

import java.util.concurrent.atomic.AtomicLong;

public class DubboRequest {

  private static final AtomicLong CNT = new AtomicLong();

  private final Long id;
  private final String path;
  private final String method;
  private final Object[] args;

  public DubboRequest(String path, String method, Object[] args) {
    this(CNT.incrementAndGet(), path, method, args);
  }

  public DubboRequest(Long id, String path, String method, Object[] args) {
    this.id = id;
    this.path = path;
    this.method = method;
    this.args = args;
  }

  public static DubboRequest decode(Long id, String path, String method, Object[] args) {
    DubboRequest request = new DubboRequest(id, path, method, args);
    return request;
  }

  public Long getId() {
    return this.id;
  }

  public String getPath() {
    return path;
  }

  public String getMethod() {
    return method;
  }

  public Object[] getArgs() {
    return args;
  }
}
