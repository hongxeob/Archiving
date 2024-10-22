package practice.paymentservice.consumer

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import practice.paymentservice.service.PaymentService
import kotlin.random.Random

@Component
class StockDecreasedConsumer(
    private val paymentService: PaymentService,
) {
    private val logger = LoggerFactory.getLogger(StockDecreasedConsumer::class.java)

    @KafkaListener(topics = ["stock-decrease"], groupId = "group-01")
    fun payment(orderId: Long) {
        try {
            errorPerHalf()
            paymentService.payment()
        } catch (e: Exception) {
            logger.error("=== [Rollback] stock-rollback, orderId => $orderId", e.message)
            paymentService.rollbackDecreasedStock(orderId)
        }
    }

    private fun errorPerHalf() {
        val zeroOrOne = Random.nextInt(2)
        if (zeroOrOne == 0) {
            throw RuntimeException("Error Payment Server")
        }
    }
}
