package org.example.example

open class Money(
    amount: Int,
    currency: String,
) : Expression {
    val amount = amount
    val currency = currency

    override fun equals(obj: Any?): Boolean {
        val money: Money = obj as Money
        return amount == money.amount &&
            currency == money.currency
    }

    fun times(multiplier: Int): Money = Money(multiplier * amount, currency)

    override fun toString(): String = "Money(amount=$amount, currency='$currency')"

    fun plus(addend: Money): Expression = Money(amount + addend.amount, currency)

    companion object {
        fun dollar(amount: Int): Money = Money(amount, "USD")

        fun franc(amount: Int): Money = Money(amount, "CHF")
    }
}
