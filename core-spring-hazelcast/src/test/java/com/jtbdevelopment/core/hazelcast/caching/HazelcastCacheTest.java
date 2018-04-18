package com.jtbdevelopment.core.hazelcast.caching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hazelcast.core.IMap;
import java.math.BigDecimal;
import java.util.concurrent.Callable;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.cache.Cache.ValueWrapper;

/**
 * Date: 2/26/15 Time: 6:41 AM
 */
public class HazelcastCacheTest {

  private static final String NAME = "cache";
  private IMap map = mock(IMap.class);
  private HazelcastCache cache = new HazelcastCache(NAME, map);
  private Object key = "X";
  private Object valueInCache = null;

  @Before
  public void setup() {
    when(map.get(key)).then(invocation -> valueInCache);
    when(map.containsKey(key)).then(invocation -> valueInCache != null);
    when(map.put(Matchers.eq(key), Matchers.any())).then(invocation -> {
      Object lastValue = valueInCache;
      valueInCache = invocation.getArguments()[1];
      return lastValue;
    });

  }

  @Test
  public void testGetNativeCache() {
    assertSame(map, cache.getNativeCache());
  }

  @Test
  public void testGet() {
    valueInCache = "gone";
    assertEquals("gone", cache.get(key).get());
  }

  @Test
  public void testGetWithNullKey() {
    assertNull(cache.get(null));
  }

  @Test
  public void testGetWithValueLoaderWhereValueInCache() {
    Object key = "X";
    valueInCache = "gone";
    Object value = cache.get(key, (Callable) () -> {
      fail("should not be called");
      return null;
    });
    assertEquals("gone", value);
  }

  @Test
  public void testGetWithValueLoaderWhereValueIsNotInCache() {
    final Object loadedValue = "new";
    Object value = cache.get(key, (Callable) () -> loadedValue);
    assertEquals(loadedValue, value);
  }

  @Test
  public void testGetWithValueLoaderWhereValueIsNotInCacheButPutInBeforeValueLoaderFinishes() {
    final Object racedIn = "racer x";
    final Object loadedValue = "new";
    Object value = cache.get(key, (Callable) () -> {
      cache.put(key, racedIn);
      return loadedValue;
    });
    assertEquals(racedIn, value);
  }

  @Test
  public void testGetWithType() {
    valueInCache = "gone";
    assertEquals(valueInCache, cache.get(key, String.class));
  }

  @Test(expected = IllegalStateException.class)
  public void testGetWithTypeNotMatching() {
    valueInCache = "gone";
    cache.get(key, Double.class);
  }

  @Test
  public void testGetWithTypeWithNulls() {
    assertNull(cache.get(null, (Class) null));
  }

  @Test
  public void testGetWithTypeWithNullKey() {
    assertNull(cache.get(null, String.class));
  }

  @Test
  public void testGetWithTypeWithNullClass() {
    Object key = "X";
    assertNull(cache.get(key, (Class) null));
  }

  @Test
  public void testPut() {
    Object newValue = new BigDecimal(0.1);
    valueInCache = "gone";
    cache.put(key, newValue);
    assertEquals(newValue, cache.get(key).get());
  }

  @Test
  public void testPutNullValues() {
    cache.put(null, null);
    verify(map, never()).put(Matchers.any(), Matchers.any());
  }

  @Test
  public void testPutNullValue() {
    String key = "X";
    cache.put(key, null);
    verify(map, never()).put(Matchers.any(), Matchers.any());
  }

  @Test
  public void testPutNullKey() {
    Double value = new Double(0.1);
    cache.put(null, value);
  }

  @Test
  public void testPutIfAbsentAndIsAbsent() {
    Object value = new Double(0.1);
    ValueWrapper wrapper = cache.putIfAbsent(key, value);
    assertNull(wrapper.get());
    verify(map).lock(key);
    verify(map).unlock(key);
  }

  @Test
  public void testPutIfAbsentAndIsAlreadyPresent() {
    Object value = new BigDecimal(0.1);
    valueInCache = new Integer(1);
    ValueWrapper wrapper = cache.putIfAbsent(key, value);
    assertNull(wrapper);
    assertEquals(1, cache.get(key).get());
    verify(map).lock(key);
    verify(map).unlock(key);
  }

  @Test
  public void testPutIfAbsentWithException() {
    Object value = new BigDecimal(0.1);
    reset(map);
    when(map.containsKey(key)).thenThrow(new RuntimeException(""));
    try {
      cache.putIfAbsent(key, value);
      fail("should have failed");
    } catch (RuntimeException e) {
      //
    }

    verify(map).lock(key);
    verify(map).unlock(key);
  }

  @Test
  public void testPutIfAbsentBothNull() {
    assertNull(cache.putIfAbsent(null, null).get());
  }

  @Test
  public void testPutIfAbsentKeyNull() {
    Object value = new Double(0.1);
    assertNull(cache.putIfAbsent(null, value).get());
  }

  @Test
  public void testPutIfAbsentValueNull() {
    Object key = "X";
    assertNull(cache.putIfAbsent(key, null).get());
  }

  @Test
  public void testEvict() {
    cache.evict(key);
    verify(map).delete(key);
  }

  @Test
  public void testEvictWithNull() {
    cache.evict(null);
    verify(map, never()).delete(Matchers.any());
  }

  @Test
  public void testClear() {
    cache.clear();
    verify(map).clear();
  }

  @Test
  public void testGetName() {
    assertEquals(NAME, cache.getName());
  }
}
