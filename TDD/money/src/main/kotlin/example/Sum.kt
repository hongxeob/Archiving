package org.example.example

class Sum(
    val augend: Money,
    val addend: Money,
) : Expression {
    override fun reduce(to: String): Money {
        val amount = augend.amount + addend.amount
        return Money(amount, to)
    }
}
