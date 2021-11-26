package com.nikondsl.cache;

public class CacheElement<K,V> {
    private final K key;
    private final V value;
    private boolean placeHolder;

    public CacheElement(K key, V value) {
        this.key = key;
        this.value = value;
        if (value == null) {
            placeHolder = true;
        }
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public boolean isPlaceHolder() {
        return placeHolder;
    }
}
