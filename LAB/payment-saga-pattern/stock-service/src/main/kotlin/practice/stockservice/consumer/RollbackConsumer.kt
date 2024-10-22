package practice.stockservice.consumer

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import practice.stockservice.service.StockService

@Component
class RollbackConsumer(
    private val stockService: StockService,
) {
    private val logger = LoggerFactory.getLogger(RollbackConsumer::class.java)

    @KafkaListener(topics = ["stock-rollback"], groupId = "group-01")
    fun rollbackDecreaseStock(orderId: Long) {
        logger.error("=== [Rollback] stock-rollback, orderId => $orderId ===")
        stockService.increase(orderId)
        stockService.rollbackCreatedOrder(orderId)
    }
}
