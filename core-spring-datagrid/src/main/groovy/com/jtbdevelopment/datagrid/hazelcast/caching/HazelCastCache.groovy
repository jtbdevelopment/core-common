package com.jtbdevelopment.datagrid.hazelcast.caching

import com.hazelcast.core.IMap
import groovy.transform.CompileStatic
import org.springframework.cache.Cache
import org.springframework.cache.support.SimpleValueWrapper

/**
 * Date: 2/25/15
 * Time: 7:12 AM
 */
@CompileStatic
class HazelCastCache implements Cache {
    private final IMap map
    final String name

    public HazelCastCache(final String name, final IMap map) {
        this.map = map
        this.name = name
    }

    @Override
    Object getNativeCache() {
        return map
    }

    @Override
    Cache.ValueWrapper get(final Object key) {
        if (key != null) {
            Object value = map.get(key)
            if (value) {
                return new SimpleValueWrapper(value)
            }
        }
        return null
    }

    @Override
    def <T> T get(final Object key, final Class<T> type) {
        if (key != null && type != null) {
            Object value = map.get(key)
            if (value) {
                if (type.isInstance(value)) {
                    return (T) value
                } else {
                    throw new IllegalStateException('Looking for ' + type + ' but found ' + value.class)
                }
            }
        }
        return null
    }

    @Override
    void put(final Object key, final Object value) {
        if (key != null && value != null) {
            map.put(key, value)
        }
    }

    @Override
    Cache.ValueWrapper putIfAbsent(final Object key, final Object value) {
        if (key == null || value == null) {
            return new SimpleValueWrapper(null)
        }
        map.lock(key)
        try {
            if (!map.containsKey(key)) {
                return new SimpleValueWrapper(map.put(key, value))
            } else {
                return new SimpleValueWrapper(map.get(key))
            }
        } finally {
            map.unlock(key)
        }
    }

    @Override
    void evict(final Object key) {
        if (key != null) {
            map.delete(key)
        }
    }

    @Override
    void clear() {
        map.clear()
    }
}
