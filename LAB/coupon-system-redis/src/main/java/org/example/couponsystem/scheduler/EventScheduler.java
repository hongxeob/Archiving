package org.example.couponsystem.scheduler;

import org.example.couponsystem.constant.Event;
import org.example.couponsystem.service.CouponService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventScheduler {
	private final CouponService couponService;

	/**
	 * 1초마다 스케줄링
	 * 쿠폰 n개가 모두 발급되면 이벤트 종료
	 * 이벤트 종료가 되지 않았다면, 쿠폰 발행 후 남은 잔여 순번 표시
	 */
	@Scheduled(fixedDelay = 1000)
	private void couponEventScheduler() {
		if (couponService.validEnd()) {
			log.info("=== 선착순 쿠폰 이벤트가 종료되었습니다. ===");
			return;
		}
		couponService.publish(Event.B_MART);
		couponService.getOrder(Event.B_MART);
	}
}
