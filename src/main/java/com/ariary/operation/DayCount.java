package com.ariary.operation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public enum DayCount {

	ACT_360, ACT_365, THIRTY_360;

	public BigDecimal yearFraction(LocalDate start, LocalDate end) {
		long days = ChronoUnit.DAYS.between(start, end);
		return switch (this) {
		case ACT_360 -> divide(days, 360);
		case ACT_365 -> divide(days, 365);
		case THIRTY_360 -> divide(days360(start, end), 360);
		};
	}

	private static BigDecimal divide(long days, int base) {
		return BigDecimal.valueOf(days).divide(BigDecimal.valueOf(base), MathContext.DECIMAL64);
	}

	private static long days360(LocalDate start, LocalDate end) {
		int d1 = Math.min(start.getDayOfMonth(), 30);
		int d2 = end.getDayOfMonth() == 31 && d1 == 30 ? 30 : end.getDayOfMonth();
		return 360L * (end.getYear() - start.getYear()) + 30L * (end.getMonthValue() - start.getMonthValue()) + d2 - d1;
	}

}
