package com.jtbdevelopment.datagrid.hazelcast

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import groovy.transform.CompileStatic
import org.springframework.beans.factory.FactoryBean
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Date: 2/25/15
 * Time: 6:45 AM
 */
@Component
@CompileStatic
class HazelCastInstanceFactoryBean implements FactoryBean<HazelcastInstance> {
    private HazelcastInstance instance

    @PostConstruct
    void setup() {
        Config config = new Config()
        //  TODO network vs cloud config
        //  TODO map config
        instance = Hazelcast.newHazelcastInstance(config)
    }

    @Override
    HazelcastInstance getObject() throws Exception {
        return instance
    }

    @Override
    Class<?> getObjectType() {
        return HazelcastInstance.class
    }

    @Override
    boolean isSingleton() {
        return true
    }
}
