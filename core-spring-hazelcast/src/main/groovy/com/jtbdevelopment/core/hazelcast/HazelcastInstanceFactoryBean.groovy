package com.jtbdevelopment.core.hazelcast

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import groovy.transform.CompileStatic
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Date: 2/25/15
 * Time: 6:45 AM
 */
@Component
@CompileStatic
class HazelcastInstanceFactoryBean implements FactoryBean<HazelcastInstance> {

    private HazelcastInstance instance

    @Autowired(required = false)
    List<HazelcastConfigurer> configurers

    @PostConstruct
    void setup() {
        Config config = new Config()
        //  TODO network vs cloud config
        configurers && configurers.each { it.modifyConfiguration(config) }
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
