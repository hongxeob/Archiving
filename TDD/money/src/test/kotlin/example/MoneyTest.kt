package example

import org.example.example.Bank
import org.example.example.Expression
import org.example.example.Money
import org.example.example.Sum
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
    fun testSimpleAddition() {
        val five = Money.dollar(5)
        val sum: Expression = five.plus(five)
        val bank = Bank()
        val reduced: Money = bank.reduce(sum, "USD")

        assertEquals(10, reduced.amount)
    }

    @Test
    fun testPlusReturnSum() {
        val five = Money.dollar(5)
        val result: Expression = five.plus(five)
        val sum = result as Sum

        assertEquals(five, sum.augend)
        assertEquals(five, sum.addend)
    }

    @Test
    fun testReduceSum() {
        val sum = Sum(Money.dollar(4), Money.dollar(3))
        val bank = Bank()
        val result = bank.reduce(sum, "USD")

        assertEquals(Money.dollar(7), result)
    }

    @Test
    fun testReduceMoney() {
        val bank = Bank()
        val result = bank.reduce(Money.dollar(1), "USD")
        assertEquals(Money.dollar(1), result)
    }
}
