package com.mng.rpc.codec;

import java.util.concurrent.atomic.AtomicLong;

public class DubboRequest {

  private static final AtomicLong CNT = new AtomicLong();

  private final Long id;
  private final String path;
  private final String method;
  private final String desc;
  private final Object[] args;

  public DubboRequest(String path, String method, String desc, Object[] args) {
    this(CNT.incrementAndGet(), path, method, desc, args);
  }

  public DubboRequest(Long id, String path, String method, String desc,
      Object[] args) {
    this.id = id;
    this.path = path;
    this.method = method;
    this.desc = desc;
    this.args = args;
  }

  public static DubboRequest decode(Long id, String path, String method,
      String desc, Object[] args) {
    DubboRequest request = new DubboRequest(id, path, method, desc, args);
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

  public String getDesc() {
    return desc;
  }

  public Object[] getArgs() {
    return args;
  }
}
