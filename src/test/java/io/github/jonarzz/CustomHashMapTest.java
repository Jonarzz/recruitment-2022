package io.github.jonarzz;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.stream.IntStream;

class CustomHashMapTest {

    @Test
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    void getFromEmptyMap() {
        Map<String, String> map = new CustomHashMap<>();

        var result = map.get("some key");

        assertThat(result)
                .isNull();
    }


    @Test
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    void removeFromEmptyMap() {
        Map<String, String> map = new CustomHashMap<>();

        var result = map.remove("some key");

        assertThat(result)
                .isNull();
    }

    @Test
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    void containsKeyOnEmptyMap() {
        Map<String, String> map = new CustomHashMap<>();

        var result = map.containsKey("some key");

        assertThat(result)
                .isFalse();
    }

    @Test
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    void containsValueOnEmptyMap() {
        Map<String, String> map = new CustomHashMap<>();

        var result = map.containsValue("some value");

        assertThat(result)
                .isFalse();
    }

    @Test
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    void sizeOfEmptyMap() {
        Map<String, String> map = new CustomHashMap<>();

        var result = map.size();

        assertThat(result)
                .isEqualTo(0);
    }

    @Test
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    void isEmptyOnEmptyMap() {
        Map<String, String> map = new CustomHashMap<>();

        var result = map.isEmpty();

        assertThat(result)
                .isTrue();
    }

    @Test
    void putFirstEntry() {
        Map<String, String> map = new CustomHashMap<>();
        var key = "key";
        var value = "value";

        var result = map.put(key, value);

        assertThat(result)
                .isNull();
        assertThat(map)
                .hasSize(1);
        assertSingleEntry(map, key, value);
    }

    @Test
    void putDuplicatedEntries() {
        Map<String, String> map = new CustomHashMap<>();
        var key = "key";
        var oldValue = "old-value";
        var intermediateValue = "value";
        var newValue = "new-value";

        map.put(key, oldValue);
        var intermediateResult = map.put(key, intermediateValue);
        var result = map.put(key, newValue);

        assertThat(intermediateResult)
                .isEqualTo(oldValue);
        assertThat(result)
                .isEqualTo(intermediateValue);
        assertThat(map)
                .hasSize(1);
        assertSingleEntry(map, key, newValue);
    }

    @Test
    void putDuplicatedEntryInSingleBucketMap() {
        Map<String, String> map = new CustomHashMap<>(1, 1);
        var key = "key";
        var oldValue = "old-value";
        var newValue = "new-value";

        map.put("filler", "filler-value");
        map.put(key, oldValue);
        var result = map.put(key, newValue);
        map.remove("non-existent");

        assertThat(result)
                .isEqualTo(oldValue);
        assertThat(map)
                .hasSize(2);
        assertSingleEntry(map, key, newValue);
    }

    @Test
    void putDuplicatedEntryWithSameValueInSingleBucketMap() {
        Map<String, String> map = new CustomHashMap<>(1, 1);
        var key = "key";
        var oldValue = "value";
        var newValue = "value";

        map.put(key, oldValue);
        map.put("filler", "filler-value");
        var result = map.put(key, newValue);

        assertThat(result)
                .isEqualTo(oldValue);
        assertThat(map)
                .hasSize(2);
        assertSingleEntry(map, key, newValue);
    }

    @Test
    void putMultipleEntries() {
        Map<String, String> map = new CustomHashMap<>();
        var key1 = "key-1";
        var value1 = "value-1";
        var key2 = "key-2";
        var value2 = "value-2";
        var key3 = "key-3";
        var value3 = "value-3";

        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);


        assertThat(map)
                .hasSize(3);
        assertSingleEntry(map, key1, value1);
        assertSingleEntry(map, key2, value2);
        assertSingleEntry(map, key3, value3);
    }

    @Test
    void putAllWithMultipleEntries() {
        Map<String, String> map = new CustomHashMap<>();
        var key1 = "key-1";
        var value1 = "value-1";
        var key2 = "key-2";
        var value2 = "value-2";
        var key3 = "key-3";
        var value3 = "value-3";

        map.putAll(Map.of(key1, value1,
                          key2, value2,
                          key3, value3));

        assertThat(map)
                .hasSize(3);
        assertSingleEntry(map, key1, value1);
        assertSingleEntry(map, key2, value2);
        assertSingleEntry(map, key3, value3);
    }

    @Test
    void putMultipleEntriesToSingleBucketMap() {
        Map<String, String> map = new CustomHashMap<>(1, 1);
        var key1 = "key-1";
        var value1 = "value-1";
        var key2 = "key-2";
        var value2 = "value-2";
        var key3 = "key-3";
        var value3 = "value-3";

        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);

        assertThat(map)
                .hasSize(3);
        assertSingleEntry(map, key1, value1);
        assertSingleEntry(map, key2, value2);
        assertSingleEntry(map, key3, value3);
        assertEntryNotInMap(map, "qwe", "123");
    }

    @Test
    void removeMultipleEntriesFromSingleBucketMap() {
        Map<String, String> map = new CustomHashMap<>(1, 1);
        var key1 = "key-1";
        var value1 = "value-1";
        var key2 = "key-2";
        var value2 = "value-2";
        var key3 = "key-3";
        var value3 = "value-3";

        map.put(key1, value1);
        map.put(key2, value2);
        map.put(key3, value3);
        map.remove(key2);
        map.remove(key1);
        map.remove(key3);

        assertThat(map.size())
                .isEqualTo(0);
        assertThat(map.isEmpty())
                .isTrue();
        assertEntryNotInMap(map, key1, value1);
        assertEntryNotInMap(map, key2, value2);
        assertEntryNotInMap(map, key3, value3);
    }

    @Test
    void longLifecycle() {
        Map<Integer, Integer> map = new CustomHashMap<>();
        var repetitions = 10;
        var entryCount = 100;
        var ints = IntStream.rangeClosed(1, entryCount)
                            .boxed()
                            .collect(toList());

        // put multiple times
        for (int repetition = 0; repetition < repetitions; repetition++) {
            Collections.shuffle(ints);
            for (var i : ints) {
                map.put(i, i);
            }

            assertThat(map.size())
                    .isEqualTo(entryCount);
            Collections.shuffle(ints);
            for (var i : ints) {
                assertSingleEntry(map, i, i);
            }
        }

        // remove all
        Collections.shuffle(ints);
        for (var i : ints) {
            map.remove(i);
        }

        assertThat(map.size())
                .isEqualTo(0);
        for (int index = 0; index < entryCount; index++) {
            assertEntryNotInMap(map, index, index);
        }
    }

    private static <K, V> void assertSingleEntry(Map<K, V> map, K key, V value) {
        assertThat(map.get(key))
                .isEqualTo(value);
        assertThat(map.containsKey(key))
                .isTrue();
        assertThat(map.containsValue(value))
                .isTrue();
        assertThat(map.keySet())
                .contains(key);
        assertThat(map.values())
                .contains(value);
        assertThat(map.entrySet())
                .extracting(Map.Entry::getKey, Map.Entry::getValue)
                .contains(
                        tuple(key, value)
                );
    }

    private static <K, V> void assertEntryNotInMap(Map<K, V> map, K key, V value) {
        assertThat(map.containsKey(key))
                .isFalse();
        assertThat(map.containsValue(value))
                .isFalse();
        assertThat(map.get(key))
                .isNull();
    }
}