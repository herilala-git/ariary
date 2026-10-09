package com.ariary.valuation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.ariary.instrument.Bond;
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
		BigDecimal value;
		if (instrument instanceof Deposit d) {
			value = depositValue(d, market);
		} else if (instrument instanceof FxForward f) {
			value = fxForwardValue(f, market);
		} else if (instrument instanceof Bond b) {
			value = bondValue(b, market);
		} else {
			throw new IllegalArgumentException("Unknown instrument " + instrument);
		}
		return value.setScale(2, RoundingMode.HALF_EVEN);
	}

	private BigDecimal depositValue(Deposit d, MarketData m) {
		BigDecimal value = discount(d.notional().add(d.interest()), d.currency(), d.maturity(), m);
		if (d.start().isAfter(m.date())) {
			value = value.subtract(discount(d.notional(), d.currency(), d.start(), m));
		}
		return value;
	}

	private BigDecimal fxForwardValue(FxForward f, MarketData m) {
		String base = f.pair().base();
		String quote = f.pair().quote();
		double dfBase = m.discountFactor(base, f.valueDate());
		double dfQuote = m.discountFactor(quote, f.valueDate());
		double marketForward = m.fxRate(base, quote).doubleValue() * dfBase / dfQuote;
		double gap = marketForward - f.forwardRate().doubleValue();
		return f.side().apply(f.notional()).multiply(BigDecimal.valueOf(gap * dfQuote));
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
