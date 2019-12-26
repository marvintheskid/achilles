package me.marvin.achilles.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class Pair<K, V> implements Map.Entry<K, V> {
    private K key;
    private V value;
}
