package org.example.example

interface Expression {
    fun reduce(
        bank: Bank,
        to: String,
    ): Money
}
