/*
 * Copyright (c) 2020. Coin Metrics Inc.
 */

package io.coinmetrics.akka.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class CircularCollectionTest: Assertions() {
    @Test
    fun listTest() {

        val list = CircularArrayList<Int>(2)

        assertNull(list.firstOrNull { it == 42 })

        run {
            val oldest = list.put(0)
            assertNull(oldest)
        }
        assertEquals(0, list.newest())
        assertNull(list.oldest())

        run {
            val oldest = list.put(1)
            assertNull(oldest)
        }
        assertEquals(1, list.newest())
        assertEquals(0, list.oldest())

        run {
            val oldest = list.put(2)
            assertEquals(0, oldest)
        }
        assertEquals(2, list.newest())
        assertEquals(1, list.oldest())

        assertEquals(1, list.firstOrNull { it == 1 })
        assertEquals(2, list.firstOrNull { it == 2 })
        assertNull(list.firstOrNull { it == 3 })

    }

    @Test
    fun `list first or null`() {
        val list = CircularArrayList<Pair<Char, Int>>(3)
        list.put('A' to  1)
        assertEquals(1, list.firstOrNull { it.first == 'A' }?.second)
        list.put('B' to  1)
        assertEquals(1, list.firstOrNull { it.first == 'A' }?.second)
        list.put('A' to  2)
        assertEquals(2, list.firstOrNull { it.first == 'A' }?.second)

        assertEquals(2, list.newest().second)
        list.putOver('A' to  3)
        assertEquals(3, list.newest().second)
        assertEquals(1, list.oldest().second)
    }

}
