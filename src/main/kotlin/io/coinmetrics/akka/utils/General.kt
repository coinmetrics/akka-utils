package io.coinmetrics.akka.utils

fun <A, B> akka.japi.Pair<A, B>.toPair(): Pair<A, B> = this.first() to this.second()
