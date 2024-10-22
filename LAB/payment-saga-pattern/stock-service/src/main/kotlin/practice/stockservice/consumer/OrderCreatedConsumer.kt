package practice.stockservice.consumer

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import practice.stockservice.service.StockService

@Component
class OrderCreatedConsumer(
    private val stockService: StockService,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["order-create"], groupId = "group-01")
    fun decrease(orderId: Long) {
        try {
            stockService.decrease(orderId)
            stockService.payment(orderId)
        } catch (e: Exception) {
            logger.error("=== [Rollback] stock-rollback, orderId => $orderId ===")
            stockService.rollbackCreatedOrder(orderId)
        }
    }
}
