package practice.paymentservice.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import practice.paymentservice.producer.StockProducer

@Service
@Transactional(readOnly = true)
class PaymentService(
    private val stockProducer: StockProducer,
) {
    private val logger = LoggerFactory.getLogger(PaymentService::class.java)

    fun payment() {
        logger.info("=== Start PaymentService ===")
    }

    fun rollbackDecreasedStock(orderId: Long) {
        stockProducer.rollbackDecreasedStock(orderId)
    }
}
