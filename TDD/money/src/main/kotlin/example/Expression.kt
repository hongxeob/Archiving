package org.example.example

import org.example.exampleopen.Money

interface Expression {
    fun reduce(
        bank: Bank,
        to: String,
    ): Money

    fun plus(addend: Expression): Expression

    fun times(multiplier: Int): Expression
}
