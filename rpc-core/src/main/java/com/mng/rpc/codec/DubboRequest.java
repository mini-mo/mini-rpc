package com.mng.rpc.codec;

import java.util.concurrent.atomic.AtomicLong;

public class DubboRequest {

  private static final AtomicLong CNT = new AtomicLong();

  private final Long id;
  private final String path;
  private final String method;
  private final Class<?> returnType;
  private final String desc;
  private final Object[] args;

  public DubboRequest(String path, String method, Class<?> returnType, String desc, Object[] args) {
    this(CNT.incrementAndGet(), path, method, returnType, desc, args);
  }

  public DubboRequest(Long id, String path, String method, Class<?> returnType, String desc,
      Object[] args) {
    this.id = id;
    this.path = path;
    this.method = method;
    this.returnType = returnType;
    this.desc = desc;
    this.args = args;
  }

  public static DubboRequest decode(Long id, String path, String method,
      String desc, Object[] args) {
    DubboRequest request = new DubboRequest(id, path, method, null, desc, args);
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

  public Class<?> getReturnType() {
    return returnType;
  }
}
