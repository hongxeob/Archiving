package com.example.statemachine.state

enum class OrderState {
    // 초기 상태
    ORDER_CREATED, // 주문생성됨

    // 정상 플로우
    ORDERED, // 주문됨 (결제 완료)
    PREPARING, // 상품준비중
    SHIPPED, // 발송됨
    DELIVERED, // 배송됨
    COMPLETED, // 거래종료

    // 이탈 상태
    ORDER_ABANDONED, // 주문이탈됨
    PAYMENT_ABANDONED, // 결제이탈됨

    // 취소 플로우
    CANCEL_REQUESTED, // 취소접수됨
    CANCELLED, // 취소됨
    CANCEL_WITHDRAWN, // 취소철회됨
    CANCEL_REJECTED, // 취소거절됨

    // 반품/교환 플로우
    RETURN_REQUESTED, // 반품/교환접수됨
    PICKUP_SCHEDULED, // 회수예약됨
    PICKUP_SHIPPED, // 회수발송됨
    PICKED_UP, // 회수됨
    PICKUP_APPROVED, // 회수승인됨
    RETURN_COMPLETED, // 반품완료
    RETURN_WITHDRAWN, // 반품/교환철회됨
    RETURN_REJECTED, // 반품/교환거절됨
}
