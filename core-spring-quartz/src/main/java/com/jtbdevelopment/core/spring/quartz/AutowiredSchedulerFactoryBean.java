package com.jtbdevelopment.core.spring.quartz;

import java.util.List;
import java.util.stream.Collectors;
import org.quartz.CronTrigger;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

/**
 * Date: 2/15/15
 * Time: 8:01 AM
 */
@Component
public class AutowiredSchedulerFactoryBean extends SchedulerFactoryBean {

    public AutowiredSchedulerFactoryBean(
        final List<CronTriggerFactoryBean> cronTriggerFactoryBeans) {
        List<CronTrigger> triggers = cronTriggerFactoryBeans
                .stream()
                .map(CronTriggerFactoryBean::getObject)
                .collect(Collectors.toList());
        super.setTriggers(triggers.toArray(new Trigger[triggers.size()]));
    }

    @Override
    public void stop() throws SchedulingException {
        super.stop();
        try {
            getScheduler().shutdown();
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }
}
