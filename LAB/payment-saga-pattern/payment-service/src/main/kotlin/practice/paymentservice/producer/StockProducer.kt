package practice.paymentservice.producer

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class StockProducer(
    private val kafkaTemplate: KafkaTemplate<String, Long>,
) {
    fun rollbackDecreasedStock(orderId: Long) {
        kafkaTemplate.send("stock-rollback", orderId)
    }
}
