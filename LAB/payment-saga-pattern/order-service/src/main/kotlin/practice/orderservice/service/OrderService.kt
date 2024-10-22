package practice.orderservice.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import practice.orderservice.domain.Order
import practice.orderservice.domain.OrderRepository
import practice.orderservice.producer.StockProducer

@Service
@Transactional(readOnly = true)
class OrderService(
    private val orderRepository: OrderRepository,
    private val stockProducer: StockProducer,
) {
    private val logger = LoggerFactory.getLogger(OrderService::class.java)

    @Transactional
    fun order(productId: String) {
        val order = Order(productId)
        val savedOrder = orderRepository.save(order)
        stockProducer.order(savedOrder.id)
    }

    @Transactional
    fun delete(id: Long) {
        orderRepository.deleteById(id)
        logger.info("Order $id 번 삭제")
    }
}
