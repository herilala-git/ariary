package com.ariary.valuation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.ariary.market.MarketData;
import com.ariary.operation.DayCount;

@Component
public class ValuationEngine {

	public BigDecimal depositValue(BigDecimal notional, BigDecimal rate, DayCount dayCount, LocalDate start,
			LocalDate maturity, String currency, MarketData market) {
		if (!maturity.isAfter(market.date())) {
			return BigDecimal.ZERO;
		}
		BigDecimal interest = notional.multiply(rate).multiply(dayCount.yearFraction(start, maturity));
		BigDecimal repayment = notional.add(interest);
		BigDecimal value = repayment.multiply(BigDecimal.valueOf(market.discountFactor(currency, maturity)));
		if (start.isAfter(market.date())) {
			value = value.subtract(notional.multiply(BigDecimal.valueOf(market.discountFactor(currency, start))));
		}
		return value.setScale(2, RoundingMode.HALF_EVEN);
	}

}
