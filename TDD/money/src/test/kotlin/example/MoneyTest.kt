package example

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MoneyTest {
    @Test
    fun testMultiplication() {
        val five: Money = Money.dollar(5)
        assertEquals(Money.dollar(10), five.times(2))
        assertEquals(Money.dollar(15), five.times(3))
    }

    @Test
    fun testFrancMultiplication() {
        val five = Money.franc(5)
        assertEquals(Money.franc(10), five.times(2))
        assertEquals(Money.franc(15), five.times(3))
    }

    @Test
    fun testEquality() {
        assertTrue(Money.dollar(5) == Money.dollar(5))
        assertFalse(Money.dollar(5) == Money.dollar(6))
        assertTrue(Money.franc(5) == Money.franc(5))
        assertFalse(Money.franc(5) == Money.franc(6))
        assertFalse(Money.franc(5).equals(Money.dollar(5)))
    }

    @Test
    fun testCurrency() {
        assertEquals("USD", Money.dollar(5).currency)
        assertEquals("CHF", Money.franc(5).currency)
    }

    @Test
    fun testDifferentClassEquality() {
        val money = Money(10, "CHF")
        val franc = Franc(10, "CHF")
        assertTrue(money == franc)
    }
}

open class Money(
    amount: Int,
    currency: String,
) {
    val amount = amount
    val currency = currency

    override fun equals(obj: Any?): Boolean {
        val money: Money = obj as Money
        return amount == money.amount &&
            currency == money.currency
    }

    fun times(multiplier: Int): Money = Money(multiplier * amount, currency)

    override fun toString(): String = "Money(amount=$amount, currency='$currency')"

    companion object {
        fun dollar(amount: Int): Dollar = Dollar(amount, "USD")

        fun franc(amount: Int): Franc = Franc(amount, "CHF")
    }
}

class Franc(
    amount: Int,
    currency: String,
) : Money(amount, currency)

class Dollar(
    amount: Int,
    currency: String,
) : Money(amount, currency)
