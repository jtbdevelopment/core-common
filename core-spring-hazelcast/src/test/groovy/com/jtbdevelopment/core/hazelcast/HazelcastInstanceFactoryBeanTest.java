package com.jtbdevelopment.core.hazelcast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import java.util.Arrays;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

/**
 * Date: 2/25/15 Time: 7:00 PM
 */
public class HazelcastInstanceFactoryBeanTest {

  private HazelcastConfigurer configurer1 = Mockito.mock(HazelcastConfigurer.class);
  private HazelcastConfigurer configurer2 = Mockito.mock(HazelcastConfigurer.class);
  private HazelcastInstanceFactoryBean factoryBean = new HazelcastInstanceFactoryBean(
      Arrays.asList(configurer1, configurer2));

  @Test
  public void testGetObjectIsSame() {
    assertFalse(factoryBean.isRunning());
    factoryBean.start();
    assertTrue(factoryBean.isRunning());
    assertEquals(factoryBean.getObject(), factoryBean.getObject());
    factoryBean.stop();
    assertFalse(factoryBean.isRunning());
  }

  @Test
  public void testStartCallsConfigurers() {
    factoryBean.start();
    org.junit.Assert.assertNotNull(factoryBean.getObject());
    verify(configurer2).modifyConfiguration(Matchers.isA(Config.class));
    verify(configurer1).modifyConfiguration(Matchers.isA(Config.class));
  }

  @Test
  public void testGetObjectType() {
    assertEquals(HazelcastInstance.class, factoryBean.getObjectType());
  }

  @Test
  public void testIsSingleton() {
    assertTrue(factoryBean.isSingleton());
  }
}
