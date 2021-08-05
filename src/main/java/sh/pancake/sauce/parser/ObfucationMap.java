/*
 * Created on Thu Aug 05 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ObfucationMap<K, V> {
    
    private BiMap<K, K> mapping;

    private Map<K, V> originalMap;

    public ObfucationMap() {
        this.mapping = HashBiMap.create();
        this.originalMap = new HashMap<>();
    }

    public K getDeobfuscated(K obfuscated) {
        return mapping.get(obfuscated);
    }

    public K getObfuscated(K deobfuscated) {
        return mapping.inverse().get(deobfuscated);
    }

    public V getFromObfuscated(K obfuscatedKey) {
        K deobfuscated = getDeobfuscated(obfuscatedKey);

        if (deobfuscated == null) return null;

        return originalMap.get(deobfuscated);
    }

    public V get(K key) {
        return originalMap.get(key);
    }

    public Set<K> originalKeys() {
        return originalMap.keySet();
    }

    public Set<K> obfuscatedKeys() {
        return mapping.keySet();
    }

    public boolean add(K obfuscated, K original, V value) {
        if (mapping.containsKey(obfuscated) || originalMap.containsKey(original)) return false;

        mapping.put(obfuscated, original);
        originalMap.put(original, value);

        return true;
    }

}
