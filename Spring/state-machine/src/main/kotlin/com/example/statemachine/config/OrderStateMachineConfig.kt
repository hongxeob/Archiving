package com.example.statemachine.config

import com.example.statemachine.event.OrderEvent
import com.example.statemachine.state.OrderState
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.statemachine.config.EnableStateMachineFactory
import org.springframework.statemachine.config.StateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer
import org.springframework.statemachine.listener.StateMachineListenerAdapter
import org.springframework.statemachine.state.State
import org.springframework.statemachine.transition.Transition

@Configuration
@EnableStateMachineFactory(name = ["orderStateMachineFactory"])
class OrderStateMachineConfig : StateMachineConfigurerAdapter<OrderState, OrderEvent>() {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * 상태 정의
     * - 초기 상태: ORDER_CREATED
     * - 종료 상태: COMPLETED, ORDER_ABANDONED, PAYMENT_ABANDONED, CANCELLED,
     *              CANCEL_WITHDRAWN, CANCEL_REJECTED, RETURN_COMPLETED,
     *              RETURN_WITHDRAWN, RETURN_REJECTED
     */
    override fun configure(states: StateMachineStateConfigurer<OrderState, OrderEvent>) {
        states
            .withStates()
            .initial(OrderState.ORDER_CREATED)
            .states(OrderState.entries.toSet())
            .end(OrderState.COMPLETED)
            .end(OrderState.ORDER_ABANDONED)
            .end(OrderState.PAYMENT_ABANDONED)
            .end(OrderState.CANCELLED)
            .end(OrderState.CANCEL_WITHDRAWN)
            .end(OrderState.CANCEL_REJECTED)
            .end(OrderState.RETURN_COMPLETED)
            .end(OrderState.RETURN_WITHDRAWN)
            .end(OrderState.RETURN_REJECTED)
    }

    override fun configure(transitions: StateMachineTransitionConfigurer<OrderState, OrderEvent>) {
        transitions
            // === 정상 주문 플로우 ===
            .withExternal()
            .source(OrderState.ORDER_CREATED)
            .target(OrderState.ORDERED)
            .event(OrderEvent.PAYMENT_COMPLETED)
            .and()
            .withExternal()
            .source(OrderState.ORDERED)
            .target(OrderState.PREPARING)
            .event(OrderEvent.PREPARE_PRODUCT)
            .and()
            .withExternal()
            .source(OrderState.PREPARING)
            .target(OrderState.SHIPPED)
            .event(OrderEvent.SHIP)
            .and()
            .withExternal()
            .source(OrderState.SHIPPED)
            .target(OrderState.DELIVERED)
            .event(OrderEvent.DELIVER)
            .and()
            .withExternal()
            .source(OrderState.DELIVERED)
            .target(OrderState.COMPLETED)
            .event(OrderEvent.COMPLETE_TRANSACTION)
            // === 이탈 플로우 ===
            .and()
            .withExternal()
            .source(OrderState.ORDER_CREATED)
            .target(OrderState.ORDER_ABANDONED)
            .event(OrderEvent.ABANDON_ORDER)
            .and()
            .withExternal()
            .source(OrderState.ORDER_CREATED)
            .target(OrderState.PAYMENT_ABANDONED)
            .event(OrderEvent.ABANDON_PAYMENT)
            // === 취소 플로우 - 취소 요청 ===
            .and()
            .withExternal()
            .source(OrderState.ORDER_CREATED)
            .target(OrderState.CANCEL_REQUESTED)
            .event(OrderEvent.PAYMENT_TIMEOUT)
            .and()
            .withExternal()
            .source(OrderState.ORDERED)
            .target(OrderState.CANCEL_REQUESTED)
            .event(OrderEvent.REQUEST_CANCEL)
            .and()
            .withExternal()
            .source(OrderState.PREPARING)
            .target(OrderState.CANCEL_REQUESTED)
            .event(OrderEvent.REQUEST_CANCEL)
            .and()
            .withExternal()
            .source(OrderState.SHIPPED)
            .target(OrderState.CANCEL_REQUESTED)
            .event(OrderEvent.REQUEST_CANCEL)
            // === 취소 플로우 - 최종 처리 ===
            .and()
            .withExternal()
            .source(OrderState.CANCEL_REQUESTED)
            .target(OrderState.CANCELLED)
            .event(OrderEvent.APPROVE_CANCEL)
            .and()
            .withExternal()
            .source(OrderState.CANCEL_REQUESTED)
            .target(OrderState.CANCEL_WITHDRAWN)
            .event(OrderEvent.WITHDRAW_CANCEL)
            .and()
            .withExternal()
            .source(OrderState.CANCEL_REQUESTED)
            .target(OrderState.CANCEL_REJECTED)
            .event(OrderEvent.REJECT_CANCEL)
            // === 반품/교환 플로우 - 요청 ===
            .and()
            .withExternal()
            .source(OrderState.DELIVERED)
            .target(OrderState.RETURN_REQUESTED)
            .event(OrderEvent.REQUEST_RETURN)
            // === 반품/교환 플로우 - 회수 프로세스 ===
            .and()
            .withExternal()
            .source(OrderState.RETURN_REQUESTED)
            .target(OrderState.PICKUP_SCHEDULED)
            .event(OrderEvent.SCHEDULE_PICKUP)
            .and()
            .withExternal()
            .source(OrderState.PICKUP_SCHEDULED)
            .target(OrderState.PICKUP_SHIPPED)
            .event(OrderEvent.SHIP_PICKUP)
            .and()
            .withExternal()
            .source(OrderState.PICKUP_SHIPPED)
            .target(OrderState.PICKED_UP)
            .event(OrderEvent.PICKUP)
            .and()
            .withExternal()
            .source(OrderState.PICKED_UP)
            .target(OrderState.PICKUP_APPROVED)
            .event(OrderEvent.APPROVE_PICKUP)
            .and()
            .withExternal()
            .source(OrderState.PICKUP_APPROVED)
            .target(OrderState.RETURN_COMPLETED)
            .event(OrderEvent.COMPLETE_RETURN)
            // === 반품/교환 플로우 - 철회/거절 ===
            .and()
            .withExternal()
            .source(OrderState.RETURN_REQUESTED)
            .target(OrderState.RETURN_WITHDRAWN)
            .event(OrderEvent.WITHDRAW_RETURN)
            .and()
            .withExternal()
            .source(OrderState.RETURN_REQUESTED)
            .target(OrderState.RETURN_REJECTED)
            .event(OrderEvent.REJECT_RETURN)
    }

    /**
     * State Machine 전역 설정
     */
    override fun configure(config: StateMachineConfigurationConfigurer<OrderState, OrderEvent>) {
        config
            .withConfiguration()
            .autoStartup(true)
            .listener(OrderStateMachineListener())
    }
}

/**
 * 상태 전이 이벤트 리스너
 */
class OrderStateMachineListener : StateMachineListenerAdapter<OrderState, OrderEvent>() {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun stateChanged(
        from: State<OrderState?, OrderEvent?>?,
        to: State<OrderState?, OrderEvent?>?,
    ) {
        logger.info("State transition: ${from?.id} -> ${to?.id}")
    }

    override fun transitionEnded(transition: Transition<OrderState, OrderEvent>?) {
        logger.info("Transition ended: ${transition?.source?.id} -> ${transition?.target?.id}")
    }

    override fun stateMachineError(
        stateMachine: org.springframework.statemachine.StateMachine<OrderState, OrderEvent>?,
        exception: Exception?,
    ) {
        logger.error("State machine error", exception)
    }
}
