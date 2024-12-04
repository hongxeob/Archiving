@file:Suppress("ktlint:standard:no-wildcard-imports")

package org.example.example

import java.util.*

class Bank {
    private val rates: Hashtable<Pair, Int> = Hashtable()

    fun reduce(
        source: Expression,
        to: String,
    ): Money = source.reduce(this, to)

    fun rate(
        from: String,
        to: String,
    ): Int {
        if (from == to) {
            return 1
        }
        val rate = rates.get(Pair(from, to)) as Int
        return rate
    }

    fun addRate(
        from: String,
        to: String,
        rate: Int,
    ) {
        rates[Pair(from, to)] = rate
    }
}

private class Pair(
    private val from: String,
    private val to: String,
) {
    override fun equals(obj: Any?): Boolean {
        val pair = obj as Pair
        return from == pair.from && to.equals(pair.to)
    }

    override fun hashCode(): Int = 0
}
