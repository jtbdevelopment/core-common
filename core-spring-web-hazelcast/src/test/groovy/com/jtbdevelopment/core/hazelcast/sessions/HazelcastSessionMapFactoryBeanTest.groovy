package com.jtbdevelopment.core.hazelcast.sessions

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import org.junit.Before
import org.junit.Test
import org.springframework.session.MapSessionRepository

import java.lang.reflect.Field

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

/**
 * Date: 3/7/15
 * Time: 8:09 PM
 */
class HazelcastSessionMapFactoryBeanTest {

    HazelcastInstance hazelcastInstance = mock(HazelcastInstance.class)
    IMap map = mock(IMap.class)
    HazelcastSessionMapFactoryBean factoryBean

    @Before
    void testSetup() {
        when(hazelcastInstance.getMap("springSessionRepository")).thenReturn(map)
        factoryBean = new HazelcastSessionMapFactoryBean(hazelcastInstance)
    }

    @Test
    void testGetObject() {
        MapSessionRepository repository = factoryBean.object
        assert repository
        assert repository.is(factoryBean.object)
        Field field = MapSessionRepository.getDeclaredField('sessions')
        field.accessible = true
        assert field.get(repository).is(map)
    }

    @Test
    void testGetObjectType() {
        assert factoryBean.objectType.is(MapSessionRepository.class)
    }

    @Test
    void testIsSingleton() {
        assert factoryBean.isSingleton()
    }
}
