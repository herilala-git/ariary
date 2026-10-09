package com.ariary.instrument;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import com.ariary.operation.DayCount;

public record Bond(String currency, BigDecimal faceValue, BigDecimal couponRate, int frequency, DayCount dayCount,
		LocalDate issueDate, LocalDate maturity) implements Instrument {

	public Bond {
		if (frequency <= 0 || 12 % frequency != 0) {
			throw new IllegalArgumentException("Invalid coupon frequency " + frequency);
		}
	}

	public List<LocalDate> couponDates() {
		return Schedule.dates(issueDate, maturity, frequency);
	}

	public BigDecimal coupon() {
		return faceValue.multiply(couponRate).divide(BigDecimal.valueOf(frequency), 2, RoundingMode.HALF_EVEN);
	}

	@Override
	public BigDecimal notional() {
		return faceValue;
	}

	@Override
	public String valueCurrency() {
		return currency;
	}

}
