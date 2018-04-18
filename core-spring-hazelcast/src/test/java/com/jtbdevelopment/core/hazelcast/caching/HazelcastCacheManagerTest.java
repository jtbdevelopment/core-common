package com.jtbdevelopment.core.hazelcast.caching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.jtbdevelopment.core.spring.caching.ListHandlingCache;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.cache.Cache;

/**
 * Date: 2/25/15 Time: 7:09 PM
 */
public class HazelcastCacheManagerTest {

  private IMap map = mock(IMap.class);
  private HazelcastInstance instance = mock(HazelcastInstance.class);
  private HazelcastCacheManager manager = new HazelcastCacheManager(instance);

  @Test
  public void testGetsNewMap() {
    String name = "named";
    Mockito.when(instance.getMap(name)).thenReturn(map);

    Cache c = manager.getCache(name);
    assertNotNull(c);
    assertTrue(c instanceof HazelcastCache);
    assertSame(map, c.getNativeCache());
  }

  @Test
  public void testGetsNewLHCMap() {
    String name = "named-LHC";
    Mockito.when(instance.getMap(name)).thenReturn(map);

    Cache c = manager.getCache(name);
    assertNotNull(c);
    assertTrue(c instanceof ListHandlingCache);
    assertSame(map, c.getNativeCache());
  }

  @Test
  public void testRepeatMapGets() {
    String name = "named";
    Mockito.when(instance.getMap(name)).thenReturn(map);

    Cache c = manager.getCache(name);
    assertNotNull(c);
    assertTrue(c instanceof HazelcastCache);
    assertEquals(c, manager.getCache(name));
    assertEquals(c, manager.getCache(name));
    assertEquals(c, manager.getCache(name));
  }

  @Test
  public void testGetMapNames() {
    Set<String> names = new HashSet<>(Arrays.asList("name1", "name2", "name3-LHC"));
    Mockito.when(instance.getMap(Matchers.any())).thenReturn(mock(IMap.class));
    names.forEach(name -> manager.getCache(name));

    assertEquals(names, manager.getCacheNames());
  }

}
