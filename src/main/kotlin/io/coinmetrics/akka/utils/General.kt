package io.coinmetrics.akka.utils

fun <A, B> akka.japi.Pair<A, B>.toPair(): Pair<A, B> = this.first() to this.second()

fun <T, R> Collection<T>.scanShim(initial: R, operation: (R, T) -> R): List<R> {
    val op: (ArrayList<R>, T) -> ArrayList<R> = { acc, e ->
        acc.add(operation(acc.lastOrNull() ?: initial, e))
        acc
    }
    return this.fold(ArrayList(this.size), op)
}
