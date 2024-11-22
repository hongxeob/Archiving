@file:Suppress("ktlint:standard:no-wildcard-imports")

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
}

abstract class Money(
    amount: Int,
) {
    val amount = amount

    override fun equals(obj: Any?): Boolean {
        val money: Money = obj as Money
        return amount == money.amount &&
            javaClass == money.javaClass
    }

    abstract fun times(multiplier: Int): Money

    companion object {
        fun dollar(amount: Int): Dollar = Dollar(amount)

        fun franc(amount: Int): Franc = Franc(amount)
    }
}

class Franc(
    amount: Int,
) : Money(amount) {
    override fun times(multiplier: Int): Franc = Franc(amount * multiplier)
}

class Dollar(
    amount: Int,
) : Money(amount) {
    override fun times(multiplier: Int): Dollar = Dollar(amount * multiplier)
}
