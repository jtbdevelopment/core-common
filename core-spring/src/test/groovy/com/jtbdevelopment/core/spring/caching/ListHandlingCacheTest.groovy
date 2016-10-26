package com.jtbdevelopment.core.spring.caching

import org.springframework.cache.concurrent.ConcurrentMapCache

import java.util.concurrent.Callable

/**
 * Date: 2/27/15
 * Time: 6:47 AM
 */
class ListHandlingCacheTest extends GroovyTestCase {
    private ConcurrentMapCache localCache = new ConcurrentMapCache('myname')
    ListHandlingCache cache = new ListHandlingCache(localCache)

    @Override
    void setUp() {
        cache.clear()
    }

    void testGetName() {
        assert localCache.getName().is(cache.getName())
    }

    void testGetNativeCache() {
        assert localCache.getNativeCache().is(cache.getNativeCache())
    }

    void testGet() {
        cache.put('2', '1')
        assert '1' == cache.get('2').get()
    }

    void testGetList() {
        cache.put('2', '1')
        cache.put('4', 10)
        assert ['1', 10] == cache.get(['2', '4']).get()
    }

    void testGetListWithNull() {
        cache.put('2', '1')
        cache.put('4', 10)
        assertNull cache.get(['2', 'junk', '4'])
    }

    void testGetArray() {
        cache.put('2', '1')
        cache.put('4', 10)
        assert ['1', 10] == cache.get(['2', '4'].toArray()).get()
    }

    void testGetArrayWithNull() {
        cache.put('2', '1')
        cache.put('4', 10)
        assertNull cache.get(['2', 'junk', '4'].toArray())
    }

    void testGetWithValueLoaderWhereValueInCache() {
        Object key = new String('X')
        Object existing = new String('gone')
        cache.put(key, existing)
        def value = cache.get(key, new Callable() {
            @Override
            Object call() throws Exception {
                fail('should not be called')
            }
        });
        assert existing.is(value)
    }

    void testGetWithValueLoaderWhereValueIsNotInCache() {
        Object key = new String('X')
        Object valueLoaded = new String('new')
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
        def value = cache.get(key, new Callable() {
            @Override
            Object call() throws Exception {
                cache.put(key, racedIn)
                return valueLoaded
            }
        });
        assert racedIn.is(value)
    }

    void testGetWithClass() {
        cache.put('2', '1')
        assert '1' == cache.get('2', String.class)
    }

    void testGetWithClassList() {
        cache.put('2', '1')
        cache.put('4', '1')
        assert ['1', '1'] == cache.get(['2', '4'], String.class)
    }

    void testGetWithClassListWithMismatch() {
        cache.put('2', '1')
        cache.put('4', 10)
        shouldFail(IllegalStateException.class, {
            cache.get(['2', '4'], String.class)
        })
    }

    void testGetWithClassListWithNull() {
        cache.put('2', '1')
        cache.put('4', '1')
        assertNull cache.get(['2', 'junk', '4'].toArray(), String.class)
    }

    void testGetWithClassArray() {
        cache.put('2', '1')
        cache.put('4', '1')
        assert ['1', '1'] == cache.get(['2', '4'].toArray(), String.class)
    }

    void testGetWithClassArrayWithMismatch() {
        cache.put('2', '1')
        cache.put('4', 10)
        shouldFail(IllegalStateException.class, {
            cache.get(['2', '4'], String.class)
        })
    }

    void testGetWithClassArrayWithNull() {
        cache.put('2', '1')
        cache.put('4', '1')
        assertNull cache.get(['2', 'junk', '4'].toArray(), String.class)
    }

    void testPut() {
        cache.put('4', '3')
        assert '3' == cache.get('4').get()
    }

    void testPutValueIsList() {
        cache.put('4', ['3'])
        assert ['3'] == cache.get('4').get()
    }

    void testPutValueIsArray() {
        cache.put('4', ['3'].toArray())
        assert ['3'].toArray() == cache.get('4').get()
    }

    void testPutOnlyKeyIsList() {
        shouldFail(IllegalArgumentException.class, {
            cache.put(['4'], '3')
        })
    }

    void testPutOnlyKeyIsArray() {
        shouldFail(IllegalArgumentException.class, {
            cache.put(['4'].toArray(), '3')
        })
    }

    void testPutList() {
        cache.put(['4', '5', '6'], [10, 'X', 32.5])
        assert 10 == cache.get('4').get()
        assert 'X' == cache.get('5').get()
        assert 32.5 == cache.get('6').get()
    }

    void testPutListSizesDoNotMatch() {
        cache.put(['4', '5', '6'], [10, 'X', 32.5, 'extra'])
        assert cache.getNativeCache().isEmpty()
    }

    void testPutArray() {
        cache.put(['4', '5', '6'].toArray(), [10, 'X', 32.5].toArray())
        assert 10 == cache.get('4').get()
        assert 'X' == cache.get('5').get()
        assert 32.5 == cache.get('6').get()
    }

    void testPutArraySizesDoNotMatch() {
        cache.put(['4', '5', '6'].toArray(), [10, 'X', 32.5, 'extra'].toArray())
        assert cache.getNativeCache().isEmpty()
    }

    void testPutIfAbsent() {
        assertNull cache.putIfAbsent('4', '3')
        assert '3' == cache.putIfAbsent('4', 'X').get()
        assert '3' == cache.get('4').get()
    }

    void testPutIfAbsentValueIsList() {
        cache.putIfAbsent('4', ['3'])
        assert ['3'] == cache.get('4').get()
    }

    void testPutIfAbsentValueIsArray() {
        cache.putIfAbsent('4', ['3'].toArray())
        assert ['3'].toArray() == cache.get('4').get()
    }

    void testPutIfAbsentOnlyKeyIsList() {
        shouldFail(IllegalArgumentException.class, {
            cache.putIfAbsent(['4'], '3')
        })
    }

    void testPutIfAbsentOnlyKeyIsArray() {
        shouldFail(IllegalArgumentException.class, {
            cache.putIfAbsent(['4'].toArray(), '3')
        })
    }

    void testPutIfAbsentList() {
        cache.put('5', 'Y')
        cache.put('6', 'S')
        assert [null, 'Y', 'S'] == cache.putIfAbsent(['4', '5', '6'], [10, 'X', 32.5]).get()
        assert 10 == cache.get('4').get()
        assert 'Y' == cache.get('5').get()
        assert 'S' == cache.get('6').get()
    }

    void testPutIfAbsentListSizesDoNotMatch() {
        cache.putIfAbsent(['4', '5', '6'], [10, 'X', 32.5, 'extra'])
        assert cache.getNativeCache().isEmpty()
    }

    void testPutIfAbsentArray() {
        cache.put('5', 'Y')
        cache.put('6', 'S')
        assert [null, 'Y', 'S'] == cache.putIfAbsent(['4', '5', '6'].toArray(), [10, 'X', 32.5].toArray()).get()
        assert 10 == cache.get('4').get()
        assert 'Y' == cache.get('5').get()
        assert 'S' == cache.get('6').get()
    }

    void testPutIfAbsentArraySizesDoNotMatch() {
        cache.putIfAbsent(['4', '5', '6'].toArray(), [10, 'X', 32.5, 'extra'].toArray())
        assert cache.getNativeCache().isEmpty()
    }

    void testEvict() {
        cache.put('1', '2')
        cache.put('3', '4')
        assert '2' == cache.get('1').get()
        assert '4' == cache.get('3').get()
        cache.evict('1')
        assertNull cache.get('1')
        assert '4' == cache.get('3').get()
    }

    void testEvictList() {
        cache.put('1', '2')
        cache.put('3', '4')
        assert '2' == cache.get('1').get()
        assert '4' == cache.get('3').get()
        cache.evict(['1', '3', 'junk'])
        assertNull cache.get('1')
        assertNull cache.get('3')
    }

    void testEvictArray() {
        cache.put('1', '2')
        cache.put('3', '4')
        assert '2' == cache.get('1').get()
        assert '4' == cache.get('3').get()
        cache.evict(['1', '3', 'junk'].toArray())
        assertNull cache.get('1')
        assertNull cache.get('3')
    }

    void testClear() {
        localCache.put('1', '2')
        localCache.put('3', '4')
        assertFalse localCache.getNativeCache().isEmpty()
        cache.clear()
        assert localCache.getNativeCache().isEmpty()
    }
}
