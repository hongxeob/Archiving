package practice.orderservice.producer

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class StockProducer(
    private val kafkaTemplate: KafkaTemplate<String, Long>,
) {
    fun order(orderId: Long) {
        kafkaTemplate.send("order-create", orderId)
    }
}
