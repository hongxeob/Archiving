package com.example.statemachine.event

enum class OrderEvent {
    // 정상 플로우 이벤트
    PAYMENT_COMPLETED, // 결제 완료
    PREPARE_PRODUCT, // 상품 준비 시작
    SHIP, // 발송
    DELIVER, // 배송 완료
    COMPLETE_TRANSACTION, // 거래 종료

    // 이탈 이벤트
    ABANDON_ORDER, // 주문 이탈
    ABANDON_PAYMENT, // 결제 이탈

    // 취소 관련 이벤트
    REQUEST_CANCEL, // 취소 요청
    PAYMENT_TIMEOUT, // 결제 기한 초과
    APPROVE_CANCEL, // 취소 승인
    WITHDRAW_CANCEL, // 취소 철회
    REJECT_CANCEL, // 취소 거절

    // 반품/교환 관련 이벤트
    REQUEST_RETURN, // 반품/교환 요청
    SCHEDULE_PICKUP, // 회수 예약
    SHIP_PICKUP, // 수거
    PICKUP, // 회수
    APPROVE_PICKUP, // 회수 승인
    COMPLETE_RETURN, // 반품 완료
    WITHDRAW_RETURN, // 반품/교환 철회
    REJECT_RETURN, // 반품/교환 거절
}
