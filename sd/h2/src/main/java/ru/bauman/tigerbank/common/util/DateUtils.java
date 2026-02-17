package ru.bauman.tigerbank.common.util;

import java.time.LocalDateTime;
import java.time.LocalTime;

public final class DateUtils {
	private DateUtils() {
	}

	public static LocalDateTime atStartOfDay(LocalDateTime date) {
		return date.toLocalDate().atStartOfDay();
	}

	public static LocalDateTime atEndOfDay(LocalDateTime date) {
		return date.toLocalDate().atTime(LocalTime.MAX);
	}
}