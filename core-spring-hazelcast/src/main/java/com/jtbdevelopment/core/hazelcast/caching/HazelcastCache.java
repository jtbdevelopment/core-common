package com.jtbdevelopment.core.hazelcast.caching;

import com.hazelcast.core.IMap;
import com.sun.istack.internal.NotNull;
import java.util.concurrent.Callable;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.lang.Nullable;

/**
 * Date: 2/25/15 Time: 7:12 AM
 */
public class HazelcastCache implements Cache {

  private final IMap map;
  private final String name;

  public HazelcastCache(final String name, final IMap map) {
    this.map = map;
    this.name = name;
  }

  @Override
  public Object getNativeCache() {
    return map;
  }

  @Override
  public ValueWrapper get(@Nullable final Object key) {
    if (key != null) {
      Object value = map.get(key);
      if (value != null) {
        return new SimpleValueWrapper(value);
      }

    }

    return null;
  }

  @Override
  public <T> T get(@Nullable final Object key, final Class<T> type) {
    if (key != null && type != null) {
      Object value = map.get(key);
      if (value != null) {
        if (type.isInstance(value)) {
          return (T) value;
        } else {
          throw new IllegalStateException("Looking for " + type + " but found " + value.getClass());
        }

      }

    }

    return null;
  }

  @Override
  public <T> T get(@NotNull Object key, @NotNull final Callable<T> valueLoader) {
    ValueWrapper wrapper = get(key);
    if (wrapper != null) {
      return (T) wrapper.get();
    }

    try {
      putIfAbsent(key, valueLoader.call());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return (T) get(key).get();
  }

  @Override
  public void put(final Object key, final Object value) {
    if (key != null && value != null) {
      map.put(key, value);
    }

  }

  @Override
  public ValueWrapper putIfAbsent(final Object key, final Object value) {
    if (key == null || value == null) {
      return new SimpleValueWrapper(null);
    }

    map.lock(key);
    try {
      if (!map.containsKey(key)) {
        return new SimpleValueWrapper(map.put(key, value));
      } else {
        return null;
      }

    } finally {
      map.unlock(key);
    }

  }

  @Override
  public void evict(final Object key) {
    if (key != null) {
      map.delete(key);
    }

  }

  @Override
  public void clear() {
    map.clear();
  }

  public final String getName() {
    return name;
  }
}
