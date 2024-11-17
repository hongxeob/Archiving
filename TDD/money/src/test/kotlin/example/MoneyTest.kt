package example

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MoneyTest {
    @Test
    fun testMultiplication() {
        val five = Dollar(5)
        five.times(2)
        assertEquals(10, five.amount)
    }
}

class Dollar(
    amount: Int,
) {
    var amount: Int = amount

    fun times(multiplier: Int) {
        this.amount *= multiplier
    }
}
