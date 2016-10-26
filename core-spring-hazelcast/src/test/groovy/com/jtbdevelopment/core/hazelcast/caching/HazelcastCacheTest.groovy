package com.jtbdevelopment.core.hazelcast.caching

import com.hazelcast.core.IMap
import org.springframework.cache.Cache

import java.util.concurrent.Callable

/**
 * Date: 2/26/15
 * Time: 6:41 AM
 */
class HazelcastCacheTest extends GroovyTestCase {
    final static String NAME = 'cache'

    void testGetNativeCache() {
        IMap map = [] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        assert map.is(cache.nativeCache)
    }

    void testGet() {
        Object key = new String('X')
        Object existing = new String('gone')
        IMap map = [
                get: {
                    Object k ->
                        assert k.is(key)
                        return existing
                }
        ] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        assert existing.is(cache.get(key).get())
    }

    void testGetWithNullKey() {
        IMap map = [] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        assertNull cache.get(null)
    }

    void testGetWithValueLoaderWhereValueInCache() {
        Object key = new String('X')
        Object existing = new String('gone')
        IMap map = [
                get: {
                    Object k ->
                        assert k.is(key)
                        return existing
                }
        ] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        def value = cache.get(key, new Callable() {
            @Override
            Object call() throws Exception {
                fail('should not be called')
                null
            }
        });
        assert existing.is(value)
    }

    void testGetWithValueLoaderWhereValueIsNotInCache() {
        Object key = new String('X')
        Object valueLoaded = new String('new')
        Object valueInCache = null
        IMap map = [
                get        : {
                    Object k ->
                        assert k.is(key)
                        return valueInCache
                },
                put        : {
                    Object k, Object v ->
                        assert k.is(key)
                        valueInCache = v
                },
                lock       : {
                    Object k ->
                        assert k.is(key)
                },
                unlock     : {
                    Object k ->
                        assert k.is(key)
                },
                containsKey: {
                    Object k ->
                        assert k.is(key)
                        return valueInCache != null
                }
        ] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        def value = cache.get(key, new Callable() {
            @Override
            Object call() throws Exception {
                return valueLoaded
            }
        });
        assert valueLoaded.is(value)
    }

    void testGetWithValueLoaderWhereValueIsNotInCacheButPutInBeforeValueLoaderFinishes() {
        Object key = new String('X')
        Object racedIn = new String('racer x')
        Object valueLoaded = new String('new')
        Object valueInCache = null
        IMap map = [
                get        : {
                    Object k ->
                        assert k.is(key)
                        return valueInCache
                },
                put        : {
                    Object k, Object v ->
                        assert k.is(key)
                        valueInCache = v
                },
                lock       : {
                    Object k ->
                        assert k.is(key)
                },
                unlock     : {
                    Object k ->
                        assert k.is(key)
                },
                containsKey: {
                    Object k ->
                        assert k.is(key)
                        return valueInCache != null
                }
        ] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        def value = cache.get(key, new Callable() {
            @Override
            Object call() throws Exception {
                cache.put(key, racedIn)
                return valueLoaded
            }
        });
        assert racedIn.is(value)
    }

    void testGetWithType() {
        Object key = new String('X')
        Object existing = new String('gone')
        IMap map = [
                get: {
                    Object k ->
                        assert k.is(key)
                        return existing
                }
        ] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        assert existing.is(cache.get(key, String.class))
    }

    void testGetWithTypeNotMatching() {
        Object key = new String('X')
        Object existing = new String('gone')
        IMap map = [
                get: {
                    Object k ->
                        assert k.is(key)
                        return existing
                }
        ] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        shouldFail(IllegalStateException.class, {
            cache.get(key, Double.class)
        })
    }

    void testGetWithTypeWithNulls() {
        IMap map = [] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        assertNull cache.get(null, (Class) null)
    }

    void testGetWithTypeWithNullKey() {
        IMap map = [] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        assertNull cache.get(null, String.class)
    }

    void testGetWithTypeWithNullClass() {
        Object key = new String('X')
        IMap map = [] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        assertNull cache.get(key, (Class) null)
    }

    void testPut() {
        boolean put = false
        Object key = new String('X')
        Object value = new Double(0.1)
        Object existing = new String('gone')
        IMap map = [
                put: {
                    Object k, Object v ->
                        assert k.is(key)
                        assert v.is(value)
                        put = true
                        return existing
                }
        ] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        cache.put(key, value)
        assert put
    }

    void testPutNullValues() {
        IMap map = [] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        cache.put(null, null)
        // No failure, no action
    }

    void testPutNullValue() {
        String key = new String('X')
        HazelcastCache cache = new HazelcastCache(NAME, null)
        cache.put(key, null)
        // No failure, no action
    }

    void testPutNullKey() {
        Double value = new Double(0.1)
        HazelcastCache cache = new HazelcastCache(NAME, null)
        cache.put(null, value)
    }

    void testPutIfAbsentAndIsAbsent() {
        boolean locked = false
        boolean unlocked = false
        boolean put = false
        Object key = new String('X')
        Object value = new Double(0.1)
        IMap map = [
                lock       : {
                    locked = true
                },
                unlock     : {
                    unlocked = true
                },
                containsKey: {
                    Object k ->
                        assert k.is(key)
                        assert locked
                        return false
                },
                put        : {
                    Object k, Object v ->
                        assert k.is(key)
                        assert v.is(value)
                        assert locked
                        put = true
                        null
                }
        ] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        Cache.ValueWrapper wrapper = cache.putIfAbsent(key, value)
        assert locked
        assert unlocked
        assert put
        assertNull wrapper.get()
    }

    void testPutIfAbsentAndIsAlreadyPresent() {
        boolean locked = false
        boolean unlocked = false
        boolean put = false
        Object key = new String('X')
        Object value = new Double(0.1)
        Object existing = new Integer(1)
        IMap map = [
                lock       : {
                    locked = true
                },
                unlock     : {
                    unlocked = true
                },
                containsKey: {
                    Object k ->
                        assert k.is(key)
                        assert locked
                        return true
                },
                get        : {
                    Object k ->
                        assert k.is(key)
                        assert locked
                        return existing
                }
        ] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        Cache.ValueWrapper wrapper = cache.putIfAbsent(key, value)
        assert locked
        assert unlocked
        assertFalse put
        assertNull wrapper
        assert existing.is(cache.get(key).get())
    }

    void testPutIfAbsentWithException() {
        boolean locked = false
        boolean unlocked = false
        Object key = new String('X')
        Object value = new Double(0.1)
        IMap map = [
                lock       : {
                    locked = true
                },
                unlock     : {
                    unlocked = true
                },
                containsKey: {
                    Object k ->
                        assert k.is(key)
                        assert locked
                        throw new RuntimeException('aaarg')
                }
        ] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        shouldFail(RuntimeException.class, {
            cache.putIfAbsent(key, value)
        })
        assert locked
        assert unlocked
    }

    void testPutIfAbsentBothNull() {
        IMap map = [] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        assertNull cache.putIfAbsent(null, null).get()
    }

    void testPutIfAbsentKeyNull() {
        Object value = new Double(0.1)
        IMap map = [] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        assertNull cache.putIfAbsent(null, value).get()
    }

    void testPutIfAbsentValueNull() {
        Object key = new String('X')
        IMap map = [] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        assertNull cache.putIfAbsent(key, null).get()
    }

    void testEvict() {
        boolean called = false
        Object key = new Integer(0)
        IMap map = [
                delete: {
                    Object k ->
                        assert k.is(key)
                        called = true
                }
        ] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        cache.evict(key)
        assert called
    }

    void testEvictWithNull() {
        boolean called = false
        IMap map = [
                delete: {
                    Object k ->
                        assert k.is(key)
                        called = true
                }
        ] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        cache.evict(null)
        assertFalse called
    }

    void testClear() {
        boolean called = false
        IMap map = [
                clear: {
                    called = true
                }
        ] as IMap
        HazelcastCache cache = new HazelcastCache(NAME, map)
        cache.clear()
        assert called
    }

    void testGetName() {
        HazelcastCache cache = new HazelcastCache(NAME, null)
        assert NAME == cache.name
    }
}
