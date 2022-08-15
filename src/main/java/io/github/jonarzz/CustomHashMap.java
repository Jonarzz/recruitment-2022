package io.github.jonarzz;

import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class CustomHashMap<K, V> implements Map<K, V> {

    public static class Entry<K, V> {

        private final K key;
        private final V value;

        private Entry<K, V> previous;
        private Entry<K, V> next;

        private Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        private void setPrevious(Entry<K, V> previous) {
            this.previous = previous;
            if (previous != null) {
                previous.next = this;
            }
        }

        private void setNext(Entry<K, V> next) {
            this.next = next;
            if (next != null) {
                next.previous = this;
            }
        }
    }

    private final int initialSize;
    private final float resizeFactor;

    private Entry<K, V>[] hashTable;

    private int size;

    public CustomHashMap() {
        this(16);
    }

    public CustomHashMap(int initialSize) {
        this(initialSize, 0.75f);
    }

    public CustomHashMap(int initialSize, float resizeFactor) {
        this.initialSize = initialSize;
        this.resizeFactor = resizeFactor;
        clear();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        var bucket = hashTable[calculateBucketIndex(key)];
        if (bucket == null) {
            return false;
        }
        var node = bucket;
        while (node != null) {
            if (node.key.equals(key)) {
                return true;
            }
            node = node.next;
        }
        return false;
    }

    @Override
    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean containsValue(Object value) {
        return streamEntries()
                .map(Entry::getValue)
                .collect(toSet())
                .contains(value);
    }

    @Override
    public V get(Object key) {
        var bucket = hashTable[calculateBucketIndex(key)];
        if (bucket == null) {
            return null;
        }
        var node = bucket;
        while (node != null) {
            if (node.key.equals(key)) {
                return node.value;
            }
            node = node.next;
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        var bucketIndex = calculateBucketIndex(key);
        var bucket = hashTable[bucketIndex];
        if (bucket == null) {
            hashTable[bucketIndex] = new Entry<>(key, value);
            increaseSize();
            return null;
        }
        if (bucket.next == null && bucket.key.equals(key)) {
            hashTable[bucketIndex] = new Entry<>(key, value);
            return bucket.value;
        }
        var node = bucket;
        while (true) {
            if (node.key.equals(key)) {
                var oldValue = node.value;
                if (oldValue.equals(value)) {
                    return oldValue;
                }
                var newEntry = new Entry<>(key, value);
                newEntry.setPrevious(node.previous);
                newEntry.setNext(node.next);
                return oldValue;
            }
            if (node.next == null) {
                node.setNext(new Entry<>(key, value));
                increaseSize();
                return null;
            }
            node = node.next;
        }
    }

    @Override
    public V remove(Object key) {
        var bucketIndex = calculateBucketIndex(key);
        var bucket = hashTable[bucketIndex];
        if (bucket == null) {
            return null;
        }
        var node = bucket;
        while (node != null) {
            if (node.key.equals(key)) {
                if (node.previous == null) {
                    hashTable[bucketIndex] = node.next;
                    if (node.next != null) {
                        node.next.setPrevious(null);
                    }
                } else {
                    node.previous.setNext(node.next);
                }
                --size;
                return node.value;
            }
            node = node.next;
        }
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        map.forEach(this::put);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void clear() {
        hashTable = new Entry[initialSize];
        size = 0;
    }

    @Override
    public Set<K> keySet() {
        return streamEntries()
                .map(Entry::getKey)
                .collect(toSet());
    }

    @Override
    public Collection<V> values() {
        return streamEntries()
                .map(Entry::getValue)
                .toList();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return streamEntries()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue()))
                .collect(toSet());
    }

    private void increaseSize() {
        ++size;
        var requiredSize = size / resizeFactor;
        if (requiredSize > hashTable.length * hashTable.length) {
            // resize table to (hashTableSize * 2)
        }
    }

    private int calculateBucketIndex(Object key) {
        return key.hashCode() % hashTable.length;
    }

    private Stream<Entry<K, V>> streamEntries() {
        Collection<Entry<K, V>> entries = new ArrayList<>();
        for (var entry : hashTable) {
            if (entry != null) {
                entries.add(entry);
                var next = entry.next;
                while (next != null) {
                    entries.add(next);
                    next = next.next;
                }
            }
        }
        return entries.stream();
    }
}
