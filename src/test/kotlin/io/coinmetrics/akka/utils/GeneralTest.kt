package io.coinmetrics.akka.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GeneralTest: Assertions() {
    @Test
    fun `scan shim`() {
        val ls = listOf(1,2,3)
        val result = ls.scanShim(1) { acc, e -> acc + e }
        println(result)
    }
}
