package com.jtbdevelopment.datagrid.hazelcast.caching

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import com.jtbdevelopment.core.spring.caching.ListHandlingCache
import org.springframework.cache.Cache

/**
 * Date: 2/25/15
 * Time: 7:09 PM
 */
class HazelcastCacheManagerTest extends GroovyTestCase {
    HazelcastCacheManager manager = new HazelcastCacheManager()

    public void testGetsNewMap() {
        String name = 'named'
        IMap map = [] as IMap
        manager.hazelCastInstance = [
                getMap: {
                    String n ->
                        assert name == n
                        return map
                }
        ] as HazelcastInstance

        Cache c = manager.getCache(name)
        assert c
        assert c instanceof HazelcastCache
        assert c.nativeCache.is(map)
    }

    public void testGetsNewLHCMap() {
        String name = 'named-LHC'
        IMap map = [] as IMap
        manager.hazelCastInstance = [
                getMap: {
                    String n ->
                        assert name == n
                        return map
                }
        ] as HazelcastInstance

        Cache c = manager.getCache(name)
        assert c
        assert c instanceof ListHandlingCache
        assert c.getNativeCache().is(map)
    }

    public void testRepeatMapGets() {
        String name = 'named'
        IMap map = [] as IMap
        manager.hazelCastInstance = [
                getMap: {
                    String n ->
                        assert name == n
                        return map
                }
        ] as HazelcastInstance

        Cache c = manager.getCache(name)
        assert c
        assert c instanceof HazelcastCache
        assert c.nativeCache.is(map)
        assert c.is(manager.getCache(name))
        assert c.is(manager.getCache(name))
        assert c.is(manager.getCache(name))
    }

    void testGetMapNames() {
        def names = ['name1', 'name2', 'name3-LHC'] as Set
        manager.hazelCastInstance = [
                getMap: {
                    String n ->
                        assert names.contains(n)
                        return [] as IMap
                }
        ] as HazelcastInstance
        names.each { manager.getCache(it) }

        assert manager.cacheNames as Set == names
    }
}
