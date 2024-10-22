package org.example.couponsystem.domain;

import org.example.couponsystem.constant.Event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventCount {
	private Event event;
	private int limit;

	private static final int END = 0;

	public synchronized void decrease() {
		this.limit--;
	}

	public boolean end() {
		return this.limit == END;
	}
}
