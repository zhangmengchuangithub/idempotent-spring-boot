package com.github.idempotent.util;

/**
 * @author zhangmc
 * @create 2020-01-20 10:31
 */
public class Entry<K, V> {

    final K key;
    final V value;
    public Entry(K key, V value){
        this.key = key;
        this.value = value;
    }

    public final K getKey() {
        return key;
    }

    public final V getValue() {
        return value;
    }

}
