@file:Suppress("ktlint:standard:no-wildcard-imports")

package example

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MoneyTest {
    @Test
    fun testMultiplication() {
        val five = Dollar(5)
        assertEquals(Dollar(10), five.times(2))
        assertEquals(Dollar(15), five.times(3))
    }

    @Test
    fun testFrancMultiplication() {
        val five = Franc(5)
        assertEquals(Franc(10), five.times(2))
        assertEquals(Franc(15), five.times(3))
    }

    @Test
    fun testEquality() {
        assertTrue(Dollar(5).equals(Dollar(5)))
        assertFalse(Dollar(5).equals(Dollar(6)))
    }
}

class Franc(
    private var amount: Int,
) {
    fun times(multiplier: Int): Franc = Franc(amount * multiplier)

    override fun equals(obj: Any?): Boolean {
        val franc: Franc = obj as Franc
        return amount == franc.amount
    }
}

class Dollar(
    private var amount: Int,
) {
    fun times(multiplier: Int): Dollar = Dollar(amount * multiplier)

    override fun equals(obj: Any?): Boolean {
        val dollar: Dollar = obj as Dollar
        return amount == dollar.amount
    }
}
