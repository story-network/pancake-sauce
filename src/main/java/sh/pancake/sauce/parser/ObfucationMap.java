/*
 * Created on Thu Aug 05 2021
 *
 * Copyright (c) storycraft. Licensed under the MIT Licence.
 */

package sh.pancake.sauce.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ObfucationMap<K, V> {

    private Set<ObfucationMap<K, V>> innerMap;
    
    private BiMap<K, K> mapping;

    private Map<K, V> originalMap;

    public ObfucationMap() {
        this.innerMap = new HashSet<>();

        this.mapping = HashBiMap.create();
        this.originalMap = new HashMap<>();
    }

    public K getDeobfuscated(K obfuscated) {
        K deobfuscated = mapping.get(obfuscated);
        if (deobfuscated != null) return deobfuscated;

        for (ObfucationMap<K, V> inner : this.innerMap) {
            K innerDeobf = inner.getDeobfuscated(obfuscated);
            if (innerDeobf != null) return innerDeobf;
        }

        return null;
    }

    public K getObfuscated(K deobfuscated) {
        K obfuscated = mapping.inverse().get(deobfuscated);

        if (obfuscated != null) return obfuscated;

        for (ObfucationMap<K, V> inner : this.innerMap) {
            K innerObf = inner.getObfuscated(deobfuscated);
            if (innerObf != null) return innerObf;
        }

        return null;
    }

    public V get(K key) {
        V value = originalMap.get(key);
        if (value != null) return originalMap.get(key);

        for (ObfucationMap<K, V> inner : this.innerMap) {
            V innerVal = inner.get(key);
            if (innerVal != null) return innerVal;
        }

        return null;
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

    public Set<ObfucationMap<K, V>> getInnerMap() {
        return innerMap;
    }

}
