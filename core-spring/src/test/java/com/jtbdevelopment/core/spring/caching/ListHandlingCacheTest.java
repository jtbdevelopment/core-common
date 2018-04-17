package com.jtbdevelopment.core.spring.caching;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.cache.concurrent.ConcurrentMapCache;

/**
 * Date: 2/27/15 Time: 6:47 AM
 */
@SuppressWarnings("ConstantConditions")
public class ListHandlingCacheTest {

  private ConcurrentMapCache localCache = new ConcurrentMapCache("myname");
  private ListHandlingCache cache = new ListHandlingCache(localCache);

  @Before
  public void setUp() {
    cache.clear();
  }

  @Test
  public void testGetName() {
    assertEquals(localCache.getName(), cache.getName());
  }

  @Test
  public void testGetNativeCache() {
    assertSame(localCache.getNativeCache(), cache.getNativeCache());
  }

  @Test
  public void testGet() {
    cache.put("2", "1");
    assertEquals("1", cache.get("2").get());
  }

  @Test
  public void testGetList() {
    cache.put("2", "1");
    cache.put("4", 10);
    assertArrayEquals(Arrays.asList("1", 10).toArray(),
        ((List) cache.get(Arrays.asList("2", "4")).get()).toArray());
  }

  @Test
  public void testGetListWithNull() {
    cache.put("2", "1");
    cache.put("4", 10);
    assertNull(cache.get(Arrays.asList("2", "junk", "4")));
  }

  @Test
  public void testGetArray() {
    cache.put("2", "1");
    cache.put("4", 10);
    Object result = cache.get(Arrays.asList("2", "4").toArray()).get();
    assertArrayEquals(Arrays.asList("1", 10).toArray(), (Object[]) result);
  }

  @Test
  public void testGetArrayWithNull() {
    cache.put("2", "1");
    cache.put("4", 10);
    assertNull(cache.get(Arrays.asList("2", "junk", "4").toArray()));
  }

  @Test
  public void testGetWithValueLoaderWhereValueInCache() {
    Object key = new String("X");
    Object existing = new String("gone");
    cache.put(key, existing);
    Object value = cache.get(key, (Callable) () -> {
      fail("should not be called");
      return null;
    });
    assertEquals(existing, value);
  }

  @Test
  public void testGetWithValueLoaderWhereValueIsNotInCache() {
    Object key = new String("X");
    final Object valueLoaded = new String("new");
    Object value = cache.get(key, (Callable) () -> valueLoaded);
    assertEquals(valueLoaded, value);
  }

  @Test
  public void testGetWithValueLoaderWhereValueIsNotInCacheButPutInBeforeValueLoaderFinishes() {
    final Object key = new String("X");
    final Object racedIn = new String("racer x");
    final Object valueLoaded = new String("new");
    Object value = cache.get(key, (Callable) () -> {
      cache.put(key, racedIn);
      return valueLoaded;
    });
    assertEquals(racedIn, value);
  }

  @Test
  public void testGetWithClass() {
    cache.put("2", "1");
    assertEquals("1", cache.get("2", String.class));
  }

  @Test
  public void testGetWithClassList() {
    cache.put("2", "1");
    cache.put("4", "1");
    Object result = cache.get(Arrays.asList("2", "4"), String.class);
    assertArrayEquals(Arrays.asList("1", "1").toArray(), ((List) result).toArray());
  }

  @Test(expected = IllegalStateException.class)
  public void testGetWithClassListWithMismatch() {
    cache.put("2", "1");
    cache.put("4", 10);
    cache.get(Arrays.asList("2", "4"), String.class);
  }

  @Test
  public void testGetWithClassListWithNull() {
    cache.put("2", "1");
    cache.put("4", "1");
    assertNull(
        cache.get(Arrays.asList("2", "junk", "4").toArray(), String.class));
  }

  @Test
  public void testGetWithClassArray() {
    cache.put("2", "1");
    cache.put("4", "1");
    Object actuals = cache.get(Arrays.asList("2", "4").toArray(), String.class);
    assertArrayEquals(Arrays.asList("1", "1").toArray(), (Object[]) actuals);
  }

  @Test(expected = IllegalStateException.class)
  public void testGetWithClassArrayWithMismatch() {
    cache.put("2", "1");
    cache.put("4", 10);
    cache.get(Arrays.asList("2", "4"), String.class);
  }

  @Test
  public void testGetWithClassArrayWithNull() {
    cache.put("2", "1");
    cache.put("4", "1");
    assertNull(
        cache.get(Arrays.asList("2", "junk", "4").toArray(), String.class));
  }

  @Test
  public void testPut() {
    cache.put("4", "3");
    assertEquals("3", cache.get("4").get());
  }

  @Test
  public void testPutValueIsList() {
    cache.put("4", Collections.singletonList("3"));
    assertArrayEquals(Collections.singletonList("3").toArray(),
        ((List) cache.get("4").get()).toArray());
  }

  @Test
  public void testPutValueIsArray() {
    cache.put("4", Collections.singletonList("3").toArray());
    assertArrayEquals(new Object[]{"3"}, (Object[]) cache.get("4").get());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPutOnlyKeyIsList() {
    cache.put(Collections.singletonList("4"), "3");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPutOnlyKeyIsArray() {
    cache.put(Collections.singletonList("4").toArray(), "3");
  }

  @Test
  public void testPutList() {
    cache.put(Arrays.asList("4", "5", "6"), Arrays.asList(10, "X", 32.5));
    assertEquals(10, cache.get("4").get());
    assertEquals("X", cache.get("5").get());
    assertEquals(32.5, cache.get("6").get());
  }

  @Test
  public void testPutListSizesDoNotMatch() {
    cache.put(Arrays.asList("4", "5", "6"), Arrays.asList(10, "X", 32.5, "extra"));
    assertTrue(((Map) cache.getNativeCache()).isEmpty());
  }

  @Test
  public void testPutArray() {
    cache.put(Arrays.asList("4", "5", "6").toArray(), Arrays.asList(10, "X", 32.5).toArray());
    assertEquals(10, cache.get("4").get());
    assertEquals("X", cache.get("5").get());
    assertEquals(32.5, cache.get("6").get());
  }

  @Test
  public void testPutArraySizesDoNotMatch() {
    cache.put(Arrays.asList("4", "5", "6").toArray(),
        Arrays.asList(10, "X", 32.5, "extra").toArray());
    assertTrue(((Map) cache.getNativeCache()).isEmpty());
  }

  @Test
  public void testPutIfAbsent() {
    assertNull(cache.putIfAbsent("4", "3"));
    assertEquals("3", cache.putIfAbsent("4", "X").get());
    assertEquals("3", cache.get("4").get());
  }

  @Test
  public void testPutIfAbsentValueIsList() {
    cache.putIfAbsent("4", Collections.singletonList("3"));
    assertArrayEquals(new Object[]{"3"}, ((List) cache.get("4").get()).toArray());
  }

  @Test
  public void testPutIfAbsentValueIsArray() {
    cache.putIfAbsent("4", Collections.singletonList("3").toArray());
    assertArrayEquals(new Object[]{"3"}, ((Object[]) cache.get("4").get()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPutIfAbsentOnlyKeyIsList() {
    cache.putIfAbsent(Collections.singletonList("4"), "3");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPutIfAbsentOnlyKeyIsArray() {
    cache.putIfAbsent(Collections.singletonList("4").toArray(), "3");
  }

  @Test
  public void testPutIfAbsentList() {
    cache.put("5", "Y");
    cache.put("6", "S");
    assertEquals(
        Arrays.asList(null, "Y", "S"),
        cache.putIfAbsent(
            Arrays.asList("4", "5", "6"),
            Arrays.asList(10, "X", 32.5)).get());
    assertEquals(10, cache.get("4").get());
    assertEquals("Y", cache.get("5").get());
    assertEquals("S", cache.get("6").get());
  }

  @Test
  public void testPutIfAbsentListSizesDoNotMatch() {
    cache.putIfAbsent(Arrays.asList("4", "5", "6"), Arrays.asList(10, "X", 32.5, "extra"));
    assertTrue(((Map) cache.getNativeCache()).isEmpty());
  }

  @Test
  public void testPutIfAbsentArray() {
    cache.put("5", "Y");
    cache.put("6", "S");
    assertEquals(
        Arrays.asList(null, "Y", "S"),
        cache.putIfAbsent(
            Arrays.asList("4", "5", "6").toArray(),
            Arrays.asList(10, "X", 32.5).toArray()).get());
    assertEquals(10, cache.get("4").get());
    assertEquals("Y", cache.get("5").get());
    assertEquals("S", cache.get("6").get());
  }

  @Test
  public void testPutIfAbsentArraySizesDoNotMatch() {
    cache.putIfAbsent(Arrays.asList("4", "5", "6").toArray(),
        Arrays.asList(10, "X", 32.5, "extra").toArray());
    assertTrue(((Map) cache.getNativeCache()).isEmpty());
  }

  @Test
  public void testEvict() {
    cache.put("1", "2");
    cache.put("3", "4");
    assertEquals("2", cache.get("1").get());
    assertEquals("4", cache.get("3").get());
    cache.evict("1");
    assertNull(cache.get("1"));
    assertEquals("4", cache.get("3").get());
  }

  @Test
  public void testEvictList() {
    cache.put("1", "2");
    cache.put("3", "4");
    assertEquals("2", cache.get("1").get());
    assertEquals("4", cache.get("3").get());
    cache.evict(Arrays.asList("1", "3", "junk"));
    assertNull(cache.get("1"));
    assertNull(cache.get("3"));
  }

  @Test
  public void testEvictArray() {
    cache.put("1", "2");
    cache.put("3", "4");
    assertEquals("2", cache.get("1").get());
    assertEquals("4", cache.get("3").get());
    cache.evict(Arrays.asList("1", "3", "junk").toArray());
    assertNull(cache.get("1"));
    assertNull(cache.get("3"));
  }

  @Test
  public void testClear() {
    localCache.put("1", "2");
    localCache.put("3", "4");
    Assert.assertFalse(localCache.getNativeCache().isEmpty());
    cache.clear();
    assertTrue(localCache.getNativeCache().isEmpty());
  }
}
