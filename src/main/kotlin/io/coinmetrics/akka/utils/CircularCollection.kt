/*
 * Copyright (c) 2020. Coin Metrics Inc.
 */

package io.coinmetrics.akka.utils


interface CircularList<V> {
    fun put(v: V): V?
    fun newest(): V?
    fun oldest(): V?
    fun <R> fold(initial: R, operation: (acc: R, V) -> R): R
    fun firstOrNull(p: (V) -> Boolean): V?
}


class CircularArrayList<V>(val capacity: Int): CircularList<V> {

    // pointer
    private var n = 0

    // array
    @Suppress("UNCHECKED_CAST")
    private val arr: Array<V> = arrayOfNulls<Any?>(capacity) as Array<V>

    override fun put(v: V): V? {
        val last = arr[n]
        arr[n] = v
        n = (n + 1) % capacity
        return last
    }

    override fun newest() = arr[(n + capacity - 1) % capacity]

    override fun oldest() = arr[n]

    override fun <R> fold(initial: R, operation: (R, V) -> R): R = arr.fold(initial, operation)

    override fun firstOrNull(p: (V) -> Boolean): V? {
        for(e in arr) { if (e != null && p(e)) return e }
        return null
    }
}



/**
 * Circular buffer with HashMap
 */
interface CircularMap<K, V> {

    // put a new element, remove and return an old element
    fun put(k: K, v: V): V?

    fun get(k: K): V?
    fun contains(k: K): Boolean
    fun size(): Int
    fun clear()

    fun isEmpty(): Boolean = size() == 0

    fun elsePut(k: K, v: V): Boolean {
        val miss = !this.contains(k)
        if (miss) this.put(k, v)
        return miss
    }

    fun <R> foldValues(initial: R, operation: (acc: R, V) -> R): R
}

class CircularHashMap<K, V>(val capacity: Int) : CircularMap<K, V> {

    // pointer
    private var n = 0

    // array
    @Suppress("UNCHECKED_CAST")
    private val arr: Array<K?> = arrayOfNulls<Any?>(capacity) as Array<K?>

    // HashMap
    private val map = HashMap<K, V>(capacity)

    override fun put(k: K, v: V): V? {
        // just return if same key
        if (map.containsKey(k)) return map.put(k, v)
        // evict old key if needed
        val prevKey = arr[n]
        val prevVal = prevKey?.let { map.remove(it) }
        arr[n] = k
        n = (n + 1) % capacity
        map.put(k, v)
        return prevVal
    }

    // get element
    override fun get(k: K): V? = map.get(k)

    // check element
    override fun contains(k: K): Boolean = map.contains(k)

    // get size
    override fun size(): Int = map.size

    // clear
    override fun clear() = map.clear()

    // fold values
    override fun <R> foldValues(initial: R, operation: (acc: R, V) -> R): R {
        var acc = initial
        map.values.forEach { acc = operation(acc, it) }
        return acc
    }

}
