package org.example.couponsystem.service;

import java.util.Set;

import org.example.couponsystem.constant.Event;
import org.example.couponsystem.domain.Coupon;
import org.example.couponsystem.domain.EventCount;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponService {
	private static final long FIRST_ELEMENT = 0;
	private static final long LAST_ELEMENT = -1;
	private static final long PUBLISH_SIZE = 10;
	private static final long LAST_INDEX = 1;

	private final RedisTemplate<String, Object> redisTemplate;
	private EventCount eventCount;

	public void setEventCount(Event event, int queue) {
		this.eventCount = new EventCount(event, queue);
	}

	public void addQueue(Event event) {
		String people = Thread.currentThread().getName();
		long now = System.currentTimeMillis();

		redisTemplate.opsForZSet().add(event.toString(), people, (int) now);
		log.info("=== 대기열에 추가 => {} ({}초)", people, now);
	}

	public void getOrder(Event event) {
		long start = FIRST_ELEMENT;
		long end = LAST_ELEMENT;

		Set<Object> queue = redisTemplate.opsForZSet()
			.range(
				event.toString(),
				start,
				end
			);

		for (Object people : queue) {
			Long rank = redisTemplate.opsForZSet().rank(event.toString(), people);

			log.info("'{}'님의 현재 남은 대기열은 {}명 입니다.", people, rank);
		}
	}

	public void publish(Event event) {
		long start = FIRST_ELEMENT;
		long end = PUBLISH_SIZE - LAST_INDEX;

		Set<Object> queue = redisTemplate.opsForZSet().range(event.toString(), start, end);
		for (Object people : queue) {
			Coupon coupon = new Coupon(event);
			log.info("'{}'님의 {} 쿠폰이 발급되었습니다 ({})", people, coupon.getEvent().getName(), coupon.getCode());
			redisTemplate.opsForZSet().remove(event.toString(), people);

			this.eventCount.decrease();
		}
	}

	public boolean validEnd() {
		return this.eventCount != null
			? this.eventCount.end()
			: false;
	}

	public long getSize(Event event) {
		return redisTemplate.opsForZSet()
			.size(event.toString());
	}
}
