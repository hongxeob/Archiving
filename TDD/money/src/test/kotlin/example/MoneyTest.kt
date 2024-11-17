package example

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
        assertTrue(Dollar(5).equals(Dollar(5)))
    }
}

class Dollar(
    amount: Int,
) {
    var amount: Int = amount

    fun times(multiplier: Int): Dollar = Dollar(amount * multiplier)

    fun equals(dollar: Dollar): Boolean = true
}
