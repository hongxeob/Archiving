package practice.stockservice.producer

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class OrderProducer(
    private val kafkaTemplate: KafkaTemplate<String, Long>,
) {
    fun rollbackCreatedOrder(orderId: Long) {
        kafkaTemplate.send("order-rollback", orderId)
    }
}
