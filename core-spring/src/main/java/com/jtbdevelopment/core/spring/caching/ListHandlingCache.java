package com.jtbdevelopment.core.spring.caching;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.lang.Nullable;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * Date: 2/26/15
 * Time: 6:42 PM
 * <p>
 * Wraps another cache to handle puts or gets that are on lists/sets/arrays
 * The list/set/array is converted into individual elements
 * <p>
 * For gets, if any item is null, the entire list is null
 * <p>
 * Does not promise atomicity across a list for put/get
 */
@SuppressWarnings("NullableProblems")
public class ListHandlingCache implements Cache {
    private static Logger logger = LoggerFactory.getLogger(ListHandlingCache.class);
    private final Cache wrapped;

    public ListHandlingCache(final Cache wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public Object getNativeCache() {
        return wrapped.getNativeCache();
    }

    @Override
    @Nullable
    public ValueWrapper get(final Object key) {
        if (key instanceof Iterable) {
            List<Object> values = getKeys((Iterable) key);
            return values == null ? null : new SimpleValueWrapper(values);
        }
        if (key instanceof Object[]) {
            List<Object> values = getKeys(Arrays.asList((Object[]) key));
            return values == null ? null : new SimpleValueWrapper(values.toArray());
        }

        return wrapped.get(key);
    }

    @Nullable
    @Override
    public <T> T get(final Object key, @Nullable final Class<T> type) {
        if (key instanceof Iterable) {
            List<T> values = getKeys((Iterable) key, type);
            //noinspection unchecked
            return (T) values;
        }
        if (key instanceof Object[]) {
            List<T> values = getKeys(Arrays.asList((Object[]) key), type);
            //noinspection unchecked
            return values == null ? null : (T) values.toArray();
        }

        return wrapped.get(key, type);
    }

    @Override
    @Nullable
    //  TODO - test
    public <T> T get(final Object key, final Callable<T> valueLoader) {
        ValueWrapper wrapper = get(key);
        if (wrapper != null) {
            //noinspection unchecked
            return (T) wrapper.get();
        }

        try {
            putIfAbsent(key, valueLoader.call());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        wrapper = get(key);
        //noinspection unchecked
        return wrapper == null ? null : (T) wrapper.get();
    }

    @Override
    public void put(final Object key, @Nullable final Object value) {
        if (key instanceof Collection && value instanceof Collection) {
            if (((Collection) key).size() != ((Collection) value).size()) {
                logger.warn("Skipping put for mismatch on keys/values " + key + "/" + value);
                return;

            }

            putElements((Collection) key, (Collection) value);
        } else if (key instanceof Collection) {
            throw new IllegalArgumentException("keys are collection but not values " + key + "/" + value);
        } else if (key instanceof Object[] && value instanceof Object[]) {
            List keys = Arrays.asList((Object[]) key);
            List values = Arrays.asList((Object[]) value);
            if (keys.size() != values.size()) {
                logger.warn("Skipping put for mismatch on keys/values " + keys + "/" + values);
                return;

            }

            putElements(keys, values);
        } else if (key instanceof Object[]) {
            throw new IllegalArgumentException("keys are array but not values");
        } else {
            wrapped.put(key, value);
        }

    }

    @Override
    public ValueWrapper putIfAbsent(final Object key, @Nullable final Object value) {
        if (key instanceof Collection && value instanceof Collection) {
            if (((Collection) key).size() != ((Collection) value).size()) {
                logger.warn("Skipping putIfAbsent for mismatch on keys/values " + key + "/" + value);
                return null;
            }

            return putElementsIfAbsent((Collection) key, (Collection) value);
        } else if (key instanceof Collection) {
            throw new IllegalArgumentException("keys are collection but not values");
        } else if (key instanceof Object[] && value instanceof Object[]) {
            List keys = Arrays.asList((Object[]) key);
            List values = Arrays.asList((Object[]) value);
            if (keys.size() != values.size()) {
                logger.warn("Skipping putIfAbsent for mismatch on keys/values " + key + "/" + value);
                return null;
            }

            return putElementsIfAbsent(keys, values);
        } else if (key instanceof Object[]) {
            throw new IllegalArgumentException("keys are array but not values");
        } else {
            return wrapped.putIfAbsent(key, value);
        }

    }

    @Override
    public void evict(final Object key) {
        if (key instanceof Iterable) {
            for (Object k : (Iterable) key) {
                wrapped.evict(k);
            }
        } else if (key instanceof Object[]) {
            for (Object k : (Object[]) key) {
                wrapped.evict(k);
            }
        } else {
            wrapped.evict(key);
        }

    }

    @Override
    public void clear() {
        wrapped.clear();
    }

    private void putElements(final Collection key, final Collection value) {
        Iterator keyIterator = key.iterator();
        Iterator valueIterator = value.iterator();
        while (keyIterator.hasNext()) {
            wrapped.put(keyIterator.next(), valueIterator.next());
        }

    }

    private ValueWrapper putElementsIfAbsent(final Collection key, final Collection value) {
        Iterator keyIterator = key.iterator();
        Iterator valueIterator = value.iterator();
        List<Object> result = new LinkedList<>();
        while (keyIterator.hasNext()) {
            ValueWrapper valueWrapper = wrapped.putIfAbsent(keyIterator.next(), valueIterator.next());
            result.add(valueWrapper != null ? valueWrapper.get() : null);
        }

        return new SimpleValueWrapper(result);
    }

    private <T> List<T> getKeys(final Iterable keys, final Class<T> classOptional) {
        List<T> results = new LinkedList<>();
        for (Object key : keys) {

            if (classOptional != null) {
                results.add(wrapped.get(key, classOptional));
            } else {
                ValueWrapper valueWrapper = wrapped.get(key);
                //noinspection unchecked
                results.add(valueWrapper != null ? (T) valueWrapper.get() : null);
            }
        }
        if (results.contains(null)) {
            return null;
        }
        return results;
    }

    private List<Object> getKeys(final Iterable keys) {
        return getKeys(keys, null);
    }
}
