package org.example.example

class Bank {
    fun reduce(
        source: Expression,
        to: String,
    ): Money = source.reduce(to)
}
