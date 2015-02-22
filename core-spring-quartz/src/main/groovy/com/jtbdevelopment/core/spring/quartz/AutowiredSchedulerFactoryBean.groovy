package com.jtbdevelopment.core.spring.quartz

import groovy.transform.CompileStatic
import org.quartz.Trigger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.quartz.CronTriggerFactoryBean
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Date: 2/15/15
 * Time: 8:01 AM
 */
@Component
@CompileStatic
class AutowiredSchedulerFactoryBean extends SchedulerFactoryBean {
    @Autowired
    List<CronTriggerFactoryBean> cronTriggerFactoryBeans

    @PostConstruct
    void setup() {
        List<? extends Trigger> triggers = cronTriggerFactoryBeans.collect { it.object }
        super.setTriggers(triggers.toArray(new Trigger[triggers.size()]))
    }
}
