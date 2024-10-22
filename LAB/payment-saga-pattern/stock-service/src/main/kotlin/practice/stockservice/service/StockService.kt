package practice.stockservice.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import practice.stockservice.producer.OrderProducer
import practice.stockservice.producer.PaymentProducer

@Service
@Transactional(readOnly = true)
class StockService(
    private val paymentProducer: PaymentProducer,
    private val orderProducer: OrderProducer,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun decrease(orderId: Long) {
        logger.info("${orderId}번 상품 재고 -1")
    }

    fun increase(orderId: Long) {
        logger.info("${orderId}번 상품 재고 +1")
    }

    fun payment(orderId: Long) {
        paymentProducer.payment(orderId)
    }

    fun rollbackCreatedOrder(orderId: Long) {
        orderProducer.rollbackCreatedOrder(orderId)
    }
}
