package practice.orderservice.consumer

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import practice.orderservice.service.OrderService

@Component
class RollbackConsumer(
    private val orderService: OrderService,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(topics = ["order-rollback"], groupId = "group-01")
    fun rollbackOrder(orderId: Long) {
        logger.error("=== [Rollback] order-rollback: orderId => $orderId")
        orderService.delete(orderId)
    }
}
