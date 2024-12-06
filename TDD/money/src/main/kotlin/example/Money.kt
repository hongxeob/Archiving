package org.example.exampleopen

import org.example.example.Bank
import org.example.example.Expression
import org.example.example.Sum

class Money(
    amount: Int,
    currency: String,
) : Expression {
    val amount = amount
    val currency = currency

    override fun reduce(
        bank: Bank,
        to: String,
    ): Money {
        val rate = bank.rate(currency, to)
        return Money(amount / rate, to)
    }

    override fun equals(obj: Any?): Boolean {
        val money: Money = obj as Money
        return amount == money.amount &&
            currency == money.currency
    }

    override fun times(multiplier: Int): Expression = Money(amount * multiplier, currency)

    override fun toString(): String = "Money(amount=$amount, currency='$currency')"

    override fun plus(addend: Expression): Expression = Sum(this, addend)

    companion object {
        fun dollar(amount: Int): Money = Money(amount, "USD")

        fun franc(amount: Int): Money = Money(amount, "CHF")
    }
}
