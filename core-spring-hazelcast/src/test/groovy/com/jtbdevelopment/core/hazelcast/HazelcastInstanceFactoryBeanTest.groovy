package com.jtbdevelopment.core.hazelcast

import com.hazelcast.config.Config
import com.hazelcast.core.HazelcastInstance

/**
 * Date: 2/25/15
 * Time: 7:00 PM
 */
class HazelcastInstanceFactoryBeanTest extends GroovyTestCase {
    HazelcastInstanceFactoryBean factoryBean = new HazelcastInstanceFactoryBean()

    private static class Configurer implements HazelcastConfigurer {
        boolean called = false

        @Override
        void modifyConfiguration(final Config config) {
            assertNotNull config
            called = true
        }
    }

    void testGetObjectIsSame() {
        assertFalse factoryBean.isRunning()
        factoryBean.setup()
        assert factoryBean.isRunning()
        assert factoryBean.object.is(factoryBean.object)
        factoryBean.stop()
    }

    void testSetupCallsConfigurers() {
        def configurers = [new Configurer(), new Configurer()]
        factoryBean.configurers = configurers
        factoryBean.setup()
        assert factoryBean.object
        assertNull configurers.find { !it.called }
    }

    void testGetObjectType() {
        assert HazelcastInstance.class.is(factoryBean.objectType)
    }

    void testIsSingleton() {
        assert factoryBean.isSingleton()
    }
}
