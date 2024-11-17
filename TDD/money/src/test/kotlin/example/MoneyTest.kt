@file:Suppress("ktlint:standard:no-wildcard-imports")

package example

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MoneyTest {
    @Test
    fun testMultiplication() {
        val five = Dollar(5)
        var product = five.times(2)
        assertEquals(10, product.amount)
        product = five.times(3)
        assertEquals(15, product.amount)
    }

    @Test
    fun testEquality() {
        assertTrue(Dollar(5).equalss(Dollar(5)))
        assertFalse(Dollar(5).equalss(Dollar(6)))
    }
}

class Dollar(
    amount: Int,
) {
    var amount: Int = amount

    fun times(multiplier: Int): Dollar = Dollar(amount * multiplier)

    fun equalss(obj: Any): Boolean {
        val dollar: Dollar = obj as Dollar
        return amount == dollar.amount
    }
}
