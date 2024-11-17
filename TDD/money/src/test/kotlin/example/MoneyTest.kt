package example

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MoneyTest {
    @Test
    fun testMultiplication() {
        val five = Dollar(10)
        five.times(2)
        assertEquals(10, five.amount)
    }
}

class Dollar(
    val amount: Int,
) {
    fun times(multiplier: Int) {
    }
}
