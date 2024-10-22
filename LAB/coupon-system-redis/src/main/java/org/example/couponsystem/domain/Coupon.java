package org.example.couponsystem.domain;

import java.util.UUID;

import org.example.couponsystem.constant.Event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Coupon {
	private Event event;
	private String code;

	public Coupon(Event event) {
		this.event = event;
		this.code = UUID.randomUUID().toString();
	}
}
