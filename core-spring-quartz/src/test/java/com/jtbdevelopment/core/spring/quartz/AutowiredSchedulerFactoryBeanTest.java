package com.jtbdevelopment.core.spring.quartz;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Date: 2/21/15 Time: 7:00 PM
 */
public class AutowiredSchedulerFactoryBeanTest {

  private AutowiredSchedulerFactoryBean schedulerFactoryBean;

  @Test
  public void testConstructor() {
    CronTriggerImpl t1 = new CronTriggerImpl();
    CronTriggerImpl t2 = new CronTriggerImpl();
    CronTriggerFactoryBean tf1 = Mockito.mock(CronTriggerFactoryBean.class);
    Mockito.when(tf1.getObject()).thenReturn(t1);
    CronTriggerFactoryBean tf2 = Mockito.mock(CronTriggerFactoryBean.class);
    Mockito.when(tf2.getObject()).thenReturn(t2);
    schedulerFactoryBean = new AutowiredSchedulerFactoryBean(Arrays.asList(tf1, tf2));
    assertArrayEquals(Arrays.asList(t1, t2).toArray(),
        ((List) ReflectionTestUtils.getField(schedulerFactoryBean, "triggers")).toArray());
  }
}
