package com.ariary.instrument;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ariary.operation.DayCount;

public record Deposit(String currency, BigDecimal notional, BigDecimal rate, DayCount dayCount, LocalDate start,
		LocalDate maturity) implements Instrument {

	public BigDecimal interest() {
		return notional.multiply(rate).multiply(dayCount.yearFraction(start, maturity));
	}

	@Override
	public String valueCurrency() {
		return currency;
	}

}
