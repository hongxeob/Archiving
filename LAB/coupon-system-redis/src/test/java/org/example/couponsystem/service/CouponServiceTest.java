package org.example.couponsystem.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.example.couponsystem.constant.Event;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CouponServiceTest {

	@Autowired
	private CouponService couponService;

	@Test
	void 선착순_쿠폰이벤트_100명에게_쿠폰_40개_제공() throws Exception {
		//given
		Event bMartEvent = Event.B_MART;
		int people = 100;
		int limitCouponCount = 40;
		CountDownLatch countDownLatch = new CountDownLatch(people);

		couponService.setEventCount(bMartEvent, limitCouponCount);

		//when
		List<Thread> workers = Stream
			.generate(() -> new Thread(new AddQueueWorker(countDownLatch, bMartEvent)))
			.limit(people)
			.toList();
		workers.forEach(Thread::start);
		countDownLatch.await();
		Thread.sleep(5000);
		long failureEventPeople = couponService.getSize(bMartEvent);

		//then
		assertThat(failureEventPeople).isEqualTo(people - limitCouponCount);
	}

	private class AddQueueWorker implements Runnable {
		private CountDownLatch countDownLatch;
		private Event event;

		public AddQueueWorker(CountDownLatch countDownLatch, Event event) {
			this.countDownLatch = countDownLatch;
			this.event = event;
		}

		@Override
		public void run() {
			couponService.addQueue(event);
			countDownLatch.countDown();
		}
	}
}
