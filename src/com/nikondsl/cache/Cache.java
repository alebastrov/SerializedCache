package com.nikondsl.cache;

public interface Cache<K,V> {
    boolean put(K key, V value) throws CompactingException;
    V get(K key) throws CompactingException;
    void remove(K key);
}
