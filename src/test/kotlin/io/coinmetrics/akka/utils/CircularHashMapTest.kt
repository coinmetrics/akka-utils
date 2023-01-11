package io.coinmetrics.akka.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.ConcurrentHashMap

internal class CircularHashMapTest : Assertions() {

    @Test
    fun size() {
        var map = CircularHashMap<Long, Long>(1000, HashMap())
        generateSequence(0L) { it -> it + 1 }.takeWhile { it < 10_000 }.forEach { map.put(it, it) }
        assertEquals(1000, map.size())

        map = CircularHashMap<Long, Long>(1000, HashMap(10))
        generateSequence(0L) { it -> it + 1 }.takeWhile { it < 10_000 }.forEach { map.put(it, it) }
        assertEquals(1000, map.size())

        map = CircularHashMap<Long, Long>(1000, HashMap(10_000))
        generateSequence(0L) { it -> it + 1 }.takeWhile { it < 10_000 }.forEach { map.put(it, it) }
        assertEquals(1000, map.size())

        map = CircularHashMap<Long, Long>(1000, HashMap(10_000))
        generateSequence(0L) { it -> it + 1 }.takeWhile { it < 100 }.forEach { map.put(it, it) }
        assertEquals(100, map.size())

        map = CircularHashMap<Long, Long>(1000, ConcurrentHashMap())
        generateSequence(0L) { it -> it + 1 }.takeWhile { it < 10_000 }.forEach { map.put(it, it) }
        assertEquals(1000, map.size())

        map = CircularHashMap<Long, Long>(1000, ConcurrentHashMap(10))
        generateSequence(0L) { it -> it + 1 }.takeWhile { it < 10_000 }.forEach { map.put(it, it) }
        assertEquals(1000, map.size())

        map = CircularHashMap<Long, Long>(1000, ConcurrentHashMap(10_000))
        generateSequence(0L) { it -> it + 1 }.takeWhile { it < 10_000 }.forEach { map.put(it, it) }
        assertEquals(1000, map.size())

        map = CircularHashMap<Long, Long>(1000, ConcurrentHashMap(10_000))
        generateSequence(0L) { it -> it + 1 }.takeWhile { it < 100 }.forEach { map.put(it, it) }
        assertEquals(100, map.size())
    }
}
