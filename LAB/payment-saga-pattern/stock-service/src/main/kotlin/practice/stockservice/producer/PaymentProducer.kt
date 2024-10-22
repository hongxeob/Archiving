package practice.stockservice.producer

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class PaymentProducer(
    private val kafkaTemplate: KafkaTemplate<String, Long>,
) {
    fun payment(orderId: Long) {
        kafkaTemplate.send("stock-decrease", orderId)
    }
}
