package com.jtbdevelopment.datagrid.hazelcast

import com.hazelcast.core.HazelcastInstance

/**
 * Date: 2/25/15
 * Time: 7:00 PM
 */
class HazelcastInstanceFactoryBeanTest extends GroovyTestCase {
    static HazelcastInstanceFactoryBean factoryBean = new HazelcastInstanceFactoryBean()

    void testGetObject() {
        assert factoryBean.object
    }

    void testGetObjectIsSame() {
        factoryBean.setup()
        assert factoryBean.object.is(factoryBean.object)
    }

    void testGetObjectType() {
        assert HazelcastInstance.class.is(factoryBean.objectType)
    }

    void testIsSingleton() {
        assert factoryBean.isSingleton()
    }
}
