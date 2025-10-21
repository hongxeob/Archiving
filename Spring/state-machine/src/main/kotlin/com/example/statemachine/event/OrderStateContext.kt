package com.example.statemachine.event

data class OrderStateContext(
    val orderId: Long,
    val userId: Long,
    val metadata: Map<String, Any> = emptyMap(),
)
