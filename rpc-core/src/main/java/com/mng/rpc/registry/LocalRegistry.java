package com.mng.rpc.registry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LocalRegistry {

  private static LocalRegistry me = new LocalRegistry();

  private Map<Class<?>, Object> providers;

  private LocalRegistry() {
    this.providers = new LinkedHashMap<>();
  }

  public static LocalRegistry getInstance() {
    return me;
  }

  public void register(Class<?> inter, Object impl) {
    providers.putIfAbsent(inter, impl);
  }

  public boolean exist(Class<?> inter) {
    return this.providers.containsKey(inter);
  }

  public Object get(Class<?> inter) {
    return providers.getOrDefault(inter, null);
  }

  public List<Class> allKeys() {
    return new ArrayList<>(providers.keySet());
  }
}
