package com.ariary.valuation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.ariary.instrument.Bond;
import com.ariary.instrument.CurrencyPair;
import com.ariary.instrument.Deposit;
import com.ariary.instrument.FxForward;
import com.ariary.instrument.Instrument;
import com.ariary.market.MarketData;

@Component
public class ValuationEngine {

	public BigDecimal value(Instrument instrument, MarketData market) {
		if (!instrument.maturity().isAfter(market.date())) {
			return BigDecimal.ZERO;
		}
		BigDecimal value = switch (instrument) {
		// pas encore commence : on enleve le nominal a verser
		case Deposit d when d.start().isAfter(market.date()) ->
			depositValue(d, market).subtract(discount(d.notional(), d.currency(), d.start(), market));
		case Deposit d -> depositValue(d, market);
		case FxForward(CurrencyPair(var base, var quote), var side, var notional, var forwardRate, var valueDate) ->
			fxForwardValue(base, quote, side.apply(notional), forwardRate, valueDate, market);
		case Bond b -> bondValue(b, market);
		};
		return value.setScale(2, RoundingMode.HALF_EVEN);
	}

	private BigDecimal depositValue(Deposit d, MarketData m) {
		return discount(d.notional().add(d.interest()), d.currency(), d.maturity(), m);
	}

	private BigDecimal fxForwardValue(String base, String quote, BigDecimal signedNotional, BigDecimal forwardRate,
			LocalDate valueDate, MarketData m) {
		double dfBase = m.discountFactor(base, valueDate);
		double dfQuote = m.discountFactor(quote, valueDate);
		double marketForward = m.fxRate(base, quote).doubleValue() * dfBase / dfQuote;
		return signedNotional.multiply(BigDecimal.valueOf((marketForward - forwardRate.doubleValue()) * dfQuote));
	}

	private BigDecimal bondValue(Bond b, MarketData m) {
		BigDecimal coupons = b.couponDates().stream().filter(d -> d.isAfter(m.date()))
				.map(d -> discount(b.coupon(), b.currency(), d, m)).reduce(BigDecimal.ZERO, BigDecimal::add);
		return coupons.add(discount(b.faceValue(), b.currency(), b.maturity(), m));
	}

	private static BigDecimal discount(BigDecimal amount, String currency, LocalDate date, MarketData m) {
		return amount.multiply(BigDecimal.valueOf(m.discountFactor(currency, date)));
	}

}
