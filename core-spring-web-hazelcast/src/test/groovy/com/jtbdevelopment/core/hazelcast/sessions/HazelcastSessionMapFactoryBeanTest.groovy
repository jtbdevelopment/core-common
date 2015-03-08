package com.jtbdevelopment.core.hazelcast.sessions

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import org.springframework.session.MapSessionRepository

import java.lang.reflect.Field

/**
 * Date: 3/7/15
 * Time: 8:09 PM
 */
class HazelcastSessionMapFactoryBeanTest extends GroovyTestCase {
    HazelcastSessionMapFactoryBean factoryBean = new HazelcastSessionMapFactoryBean()

    void testSetup() {
    }

    void testGetObject() {
        IMap map = [] as IMap
        factoryBean.hazelcastInstance = [
                getMap: {
                    String name ->
                        assert name == 'springSessionRepository'
                        return map
                }
        ] as HazelcastInstance
        factoryBean.setup()
        MapSessionRepository repository = factoryBean.object
        assert repository
        assert repository.is(factoryBean.object)
        Field field = MapSessionRepository.getDeclaredField('sessions')
        field.accessible = true
        assert field.get(repository).is(map)
    }

    void testGetObjectType() {
        assert factoryBean.objectType.is(MapSessionRepository.class)
    }

    void testIsSingleton() {
        assert factoryBean.isSingleton()
    }
}
