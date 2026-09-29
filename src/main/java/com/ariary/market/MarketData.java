package com.ariary.market;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.NoSuchElementException;

public record MarketData(LocalDate date, Map<String, BigDecimal> fxRates, Map<String, YieldCurve> curves) {

	private static final String PIVOT = "CAD";

	public MarketData {
		fxRates = Map.copyOf(fxRates);
		curves = Map.copyOf(curves);
	}

	public BigDecimal fxRate(String base, String quote) {
		if (base.equals(quote)) {
			return BigDecimal.ONE;
		}
		BigDecimal direct = fxRates.get(base + "/" + quote);
		if (direct != null) {
			return direct;
		}
		BigDecimal inverse = fxRates.get(quote + "/" + base);
		if (inverse != null) {
			return BigDecimal.ONE.divide(inverse, MathContext.DECIMAL64);
		}
		if (!base.equals(PIVOT) && !quote.equals(PIVOT)) {
			return fxRate(base, PIVOT).multiply(fxRate(PIVOT, quote), MathContext.DECIMAL64);
		}
		throw new NoSuchElementException("No FX rate " + base + "/" + quote + " on " + date);
	}

	public YieldCurve curve(String currency) {
		YieldCurve curve = curves.get(currency);
		if (curve == null) {
			throw new NoSuchElementException("No " + currency + " curve on " + date);
		}
		return curve;
	}

	public double discountFactor(String currency, LocalDate payDate) {
		return curve(currency).discountFactor(ChronoUnit.DAYS.between(date, payDate));
	}

}
