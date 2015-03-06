package com.jtbdevelopment.datagrid.hazelcast.caching

import com.hazelcast.core.HazelcastInstance
import com.hazelcast.core.IMap
import com.jtbdevelopment.core.spring.caching.ListHandlingCache
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Component

import java.util.concurrent.ConcurrentHashMap

/**
 * Date: 2/25/15
 * Time: 7:13 AM
 *
 * Create cache from hazelcast - if ends in LHC, wrap in ListHandlingCache as well
 */
@Component
@CompileStatic
class HazelCastCacheManager implements CacheManager {
    public static final String LIST_HANDLING_CACHE_SUFFIX = '-LHC'
    @Autowired
    HazelcastInstance hazelCastInstance

    private ConcurrentHashMap<String, Cache> caches = new ConcurrentHashMap<>()

    @Override
    Cache getCache(final String name) {
        if (!caches.containsKey(name)) {
            IMap map = hazelCastInstance.getMap(name)
            if (map == null) {
                throw new IllegalStateException('Could not get hazelcast map')
            }
            Cache wrapper = new HazelCastCache(name, map)
            if (name.endsWith(LIST_HANDLING_CACHE_SUFFIX)) {
                wrapper = new ListHandlingCache(wrapper)
            }
            caches.putIfAbsent(name, wrapper)
        }
        return caches[name]
    }

    @Override
    Collection<String> getCacheNames() {
        return caches.keySet()
    }
}
