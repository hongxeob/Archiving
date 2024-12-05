package org.example.example

import org.example.exampleopen.Money

class Sum(
    val augend: Expression,
    val addend: Expression,
) : Expression {
    override fun reduce(
        bank: Bank,
        to: String,
    ): Money {
        val amount = augend.reduce(bank, to).amount + addend.reduce(bank, to).amount
        return Money(amount, to)
    }

    override fun plus(addend: Expression): Expression = Sum(this, addend)
}
