/*
 * Copyright (c) 2022. Coin Metrics Inc.
 */

package io.coinmetrics.akka.utils

interface CircularList<V> {
    fun put(v: V): V?
    fun putOver(v: V)
    fun newest(): V?
    fun oldest(): V?
    fun <R> fold(initial: R, operation: (acc: R, V) -> R): R
    fun firstOrNull(p: (V) -> Boolean): V?
}

class CircularArrayList<V>(val capacity: Int) : CircularList<V> {

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

    override fun putOver(v: V) {
        arr[(n + capacity - 1) % capacity] = v
    }

    override fun newest() = arr[(n + capacity - 1) % capacity]

    override fun oldest() = arr[n]

    override fun <R> fold(initial: R, operation: (R, V) -> R): R = arr.fold(initial, operation)

    override fun firstOrNull(p: (V) -> Boolean): V? {
        for (i in n + 1..n + capacity) {
            val e = arr[i % capacity]
            if (e != null && p(e)) return e
        }
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

class CircularHashMap<K, V>(val capacity: Int, private val map: MutableMap<K, V> = HashMap(capacity)) :
    CircularMap<K, V> {

    // pointer
    private var n = 0

    // array
    @Suppress("UNCHECKED_CAST")
    private val arr: Array<K?> = arrayOfNulls<Any?>(capacity) as Array<K?>

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
    override fun clear() {
        map.clear()
        n = 0
        arr.fill(null)
    }

    // fold values
    override fun <R> foldValues(initial: R, operation: (acc: R, V) -> R): R {
        var acc = initial
        (n until n + capacity).forEach { i ->
            val key = arr[(n + i) % capacity]
            key?.let {
                val value = map.getValue(it)
                acc = operation(acc, value)
            }
        }
        return acc
    }
}
