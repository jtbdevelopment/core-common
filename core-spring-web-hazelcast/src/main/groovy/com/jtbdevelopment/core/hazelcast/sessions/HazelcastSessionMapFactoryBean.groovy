package com.jtbdevelopment.core.hazelcast.sessions

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import groovy.transform.CompileStatic
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.session.ExpiringSession
import org.springframework.session.MapSessionRepository
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

/**
 * Date: 3/6/15
 * Time: 6:34 PM
 */
@Component
@CompileStatic
class HazelcastSessionMapFactoryBean implements FactoryBean<MapSessionRepository> {
    public static final String MAP_NAME = "springSessionRepository"
    @Autowired
    HazelcastInstance hazelcastInstance

    private MapSessionRepository mapSessionRepository

    @PostConstruct
    void setup() {
        IMap<String, ExpiringSession> map = hazelcastInstance.getMap(MAP_NAME)
        mapSessionRepository = new MapSessionRepository(map)
    }

    @Override
    MapSessionRepository getObject() throws Exception {
        return mapSessionRepository
    }

    @Override
    Class<?> getObjectType() {
        return MapSessionRepository.class
    }

    @Override
    boolean isSingleton() {
        return true
    }
}
