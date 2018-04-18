package com.jtbdevelopment.core.hazelcast.sessions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.junit.Before;
import org.junit.Test;
import org.springframework.session.MapSessionRepository;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Date: 3/7/15 Time: 8:09 PM
 */
public class HazelcastSessionMapFactoryBeanTest {

  private HazelcastInstance hazelcastInstance = mock(HazelcastInstance.class);
  private IMap map = mock(IMap.class);
  private HazelcastSessionMapFactoryBean factoryBean;

  @Before
  public void testSetup() {
    when(hazelcastInstance.getMap("springSessionRepository")).thenReturn(map);
    factoryBean = new HazelcastSessionMapFactoryBean(hazelcastInstance);
  }

  @Test
  public void testGetObject() throws Exception {
    MapSessionRepository repository = factoryBean.getObject();
    assertNotNull(repository);
    assertEquals(factoryBean.getObject(), repository);
    assertEquals(map, ReflectionTestUtils.getField(repository, "sessions"));
  }

  @Test
  public void testGetObjectType() {
    assertEquals(MapSessionRepository.class, factoryBean.getObjectType());
  }

  @Test
  public void testIsSingleton() {
    assertTrue(factoryBean.isSingleton());
  }
}
