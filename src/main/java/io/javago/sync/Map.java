package io.javago.sync;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * The {@code Map} class implements Go's {@code sync.Map}.
 * A thread-safe map that extends {@link ConcurrentHashMap} and provides additional utility methods.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 */
public class Map<K, V> extends ConcurrentHashMap<K, V> {

	/**
	 * Creates a new, empty map with the default initial capacity, load factor, and concurrency level.
	 */
	public Map() {}

	/**
	 * Creates a new, empty map with the specified initial capacity, and with the default load factor and concurrency
	 * level.
	 *
	 * @param initialCapacity the initial capacity. The implementation performs internal sizing to accommodate this many elements.
	 */
	public Map(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Creates a new map with the same mappings as the specified map.
	 *
	 * @param m the map whose mappings are to be placed in this map
	 */
	public Map(java.util.Map<? extends K, ? extends V> m) {
		super(m);
	}

	/**
	 * Creates a new, empty map with the specified initial capacity and load factor, and with the default concurrency
	 * level.
	 *
	 * @param initialCapacity the initial capacity. The implementation performs internal sizing to accommodate this many elements.
	 * @param loadFactor the load factor threshold, used to control resizing. Resizing may be performed when the average number of elements per bin exceeds this threshold.
	 */
	public Map(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Creates a new, empty map with the specified initial capacity, load factor, and concurrency level.
	 *
	 * @param initialCapacity the initial capacity. The implementation performs internal sizing to accommodate this many elements.
	 * @param loadFactor the load factor threshold, used to control resizing. Resizing may be performed when the average number of elements per bin exceeds this threshold.
	 * @param concurrencyLevel the estimated number of concurrently updating threads. The implementation performs internal sizing to try to accommodate this many threads.
	 */
	public Map(int initialCapacity, float loadFactor, int concurrencyLevel) {
		super(initialCapacity, loadFactor, concurrencyLevel);
	}

	/**
	 * Removes the entry for a key only if currently mapped to a given value.
	 *
	 * @param key the key whose associated value is to be removed
	 * @param value the value expected to be associated with the specified key
	 * @return {@code true} if the value was removed
	 */
	public boolean compareAndDelete(K key, V value) {
		return super.remove(key, value);
	}

	/**
	 * Replaces the entry for a key only if currently mapped to a given value.
	 *
	 * @param key the key with which the specified value is associated
	 * @param oldValue the value expected to be associated with the specified key
	 * @param newValue the value to be associated with the specified key
	 * @return {@code true} if the value was replaced
	 */
	public boolean compareAndSwap(K key, V oldValue, V newValue) {
		return super.replace(key, oldValue, newValue);
	}

	/**
	 * Removes the mapping for a key from this map if it is present.
	 *
	 * @param key the key whose mapping is to be removed from the map
	 */
	public void delete(K key) {
		super.remove(key);
	}

	/**
	 * Returns the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the
	 * key.
	 *
	 * @param key the key whose associated value is to be returned
	 * @return the value to which the specified key is mapped, or {@code null} if this map contains no mapping for the key
	 */
	public V load(K key) {
		return super.get(key);
	}

	/**
	 * Removes the mapping for a key from this map if it is present and returns the associated value.
	 *
	 * @param key the key whose mapping is to be removed from the map
	 * @return the previous value associated with {@code key}, or {@code null} if there was no mapping for {@code key}
	 */
	public V loadAndDelete(K key) {
		return super.remove(key);
	}

	/**
	 * If the specified key is not already associated with a value, associates it with the given value and returns
	 * {@code null}, else returns the current value.
	 *
	 * @param key the key with which the specified value is to be associated
	 * @param value the value to be associated with the specified key
	 * @return the previous value associated with the specified key, or {@code null} if there was no mapping for the key
	 */
	public V loadOrStore(K key, V value) {
		return super.putIfAbsent(key, value);
	}

	/**
	 * Performs the given action for each entry in this map until all entries have been processed or the action throws
	 * an exception.
	 *
	 * @param consumer the action to be performed for each entry
	 */
	public void range(BiConsumer<? super K, ? super V> consumer) {
		super.forEach(consumer);
	}

	/**
	 * Associates the specified value with the specified key in this map.
	 *
	 * @param key the key with which the specified value is to be associated
	 * @param value the value to be associated with the specified key
	 */
	public void store(K key, V value) {
		super.put(key, value);
	}

	/**
	 * Associates the specified value with the specified key in this map and returns the previous value associated with
	 * the key, or {@code null} if there was no mapping for the key.
	 *
	 * @param key the key with which the specified value is to be associated
	 * @param value the value to be associated with the specified key
	 * @return the previous value associated with {@code key}, or {@code null} if there was no mapping for {@code key}
	 */
	public V swap(K key, V value) {
		return super.put(key, value);
	}
}
