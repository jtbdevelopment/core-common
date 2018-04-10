package com.jtbdevelopment.core.spring.quartz

import org.quartz.impl.triggers.CronTriggerImpl
import org.springframework.scheduling.quartz.CronTriggerFactoryBean
import org.springframework.scheduling.quartz.SchedulerAccessor

import java.lang.reflect.Field

/**
 * Date: 2/21/15
 * Time: 7:00 PM
 */
class AutowiredSchedulerFactoryBeanTest extends GroovyTestCase {
    AutowiredSchedulerFactoryBean schedulerFactoryBean

    void testConstructor() {
        def t1 = new CronTriggerImpl()
        def t2 = new CronTriggerImpl()
        def tf1 = [
                getObject: { t1 }
        ] as CronTriggerFactoryBean
        def tf2 = [
                getObject: { t2 }
        ] as CronTriggerFactoryBean
        schedulerFactoryBean = new AutowiredSchedulerFactoryBean([tf1, tf2])
        Field field = SchedulerAccessor.getDeclaredField('triggers')
        field.accessible = true
        assert field.get(schedulerFactoryBean) == [t1, t2]
    }
}
