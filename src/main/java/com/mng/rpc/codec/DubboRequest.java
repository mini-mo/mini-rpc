package com.mng.rpc.codec;

import java.util.concurrent.atomic.AtomicLong;

public class DubboRequest {

  private static final AtomicLong CNT = new AtomicLong();

  private final Long id;
  private final String path;
  private final String method;
  private final String methodParameterDescriptor;
  private final Object[] args;

  public DubboRequest(String path, String method, String methodParameterDescriptor, Object[] args) {
    this(CNT.incrementAndGet(), path, method, methodParameterDescriptor, args);
  }

  public DubboRequest(Long id, String path, String method, String methodParameterDescriptor,
      Object[] args) {
    this.id = id;
    this.path = path;
    this.method = method;
    this.methodParameterDescriptor = methodParameterDescriptor;
    this.args = args;
  }

  public static DubboRequest decode(Long id, String path, String method,
      String methodParameterDescriptor, Object[] args) {
    DubboRequest request = new DubboRequest(id, path, method, methodParameterDescriptor, args);
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

  public String getMethodParameterDescriptor() {
    return methodParameterDescriptor;
  }

  public Object[] getArgs() {
    return args;
  }
}
