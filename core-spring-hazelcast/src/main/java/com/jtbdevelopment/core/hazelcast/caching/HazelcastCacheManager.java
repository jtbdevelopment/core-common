package com.jtbdevelopment.core.hazelcast.caching;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.jtbdevelopment.core.spring.caching.ListHandlingCache;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Date: 2/25/15
 * Time: 7:13 AM
 * <p>
 * Create cache from hazelcast - if ends in LHC, wrap in ListHandlingCache as well
 */
@Component
public class HazelcastCacheManager implements CacheManager {
    private static final String LIST_HANDLING_CACHE_SUFFIX = "-LHC";
    private final HazelcastInstance hazelCastInstance;
    private ConcurrentHashMap<String, Cache> caches = new ConcurrentHashMap<>();

    public HazelcastCacheManager(final HazelcastInstance hazelCastInstance) {
        this.hazelCastInstance = hazelCastInstance;
    }

    @Override
    @Nullable
    public Cache getCache(final String name) {
        if (!caches.containsKey(name)) {
            IMap map = hazelCastInstance.getMap(name);
            if (map == null) {
                throw new IllegalStateException("Could not get hazelcast map");
            }

            Cache wrapper = new HazelcastCache(name, map);
            if (name.endsWith(LIST_HANDLING_CACHE_SUFFIX)) {
                wrapper = new ListHandlingCache(wrapper);
            }

            caches.putIfAbsent(name, wrapper);
        }

        return (caches.get(name));
    }

    @Override
    public Collection<String> getCacheNames() {
        return caches.keySet();
    }
}
