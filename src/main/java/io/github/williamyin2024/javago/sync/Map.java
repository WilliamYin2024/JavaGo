package io.github.williamyin2024.javago.sync;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class Map<K, V> extends ConcurrentHashMap<K, V> {

	public Map() {}

	public Map(int initialCapacity) {
		super(initialCapacity);
	}

	public Map(java.util.Map<? extends K, ? extends V> m) {
		super(m);
	}

	public Map(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public Map(int initialCapacity, float loadFactor, int concurrencyLevel) {
		super(initialCapacity, loadFactor, concurrencyLevel);
	}

	public boolean compareAndDelete(K key, V value) {
		return super.remove(key, value);
	}

	public boolean compareAndSwap(K key, V oldValue, V newValue) {
		return super.replace(key, oldValue, newValue);
	}

	public void delete(K key) {
		super.remove(key);
	}

	public V load(K key) {
		return super.get(key);
	}

	public V loadAndDelete(K key) {
		return super.remove(key);
	}

	public V loadOrStore(K key, V value) {
		return super.putIfAbsent(key, value);
	}

	public void range(BiConsumer<? super K, ? super V> consumer) {
		super.forEach(consumer);
	}

	public void store(K key, V value) {
		super.put(key, value);
	}

	public V swap(K key, V value) {
		return super.put(key, value);
	}
}
