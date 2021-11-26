package com.nikondsl.cache;

public interface Cache<K,V> {
    boolean put(K key, V value) throws CompactingException;
    V get(K key) throws CompactingException, ReflectiveOperationException;
    void remove(K key);
    void flush();
}
