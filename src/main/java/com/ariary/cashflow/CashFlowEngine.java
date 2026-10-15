package com.ariary.cashflow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.ariary.instrument.Bond;
import com.ariary.instrument.CurrencyPair;
import com.ariary.instrument.Deposit;
import com.ariary.instrument.FxForward;
import com.ariary.instrument.Instrument;

@Component
public class CashFlowEngine {

	public List<CashFlow> cashFlows(Instrument instrument) {
		return switch (instrument) {
		case Deposit d ->
			List.of(new CashFlow(d.start(), d.currency(), d.notional().negate()), new CashFlow(d.maturity(),
					d.currency(), d.notional().add(d.interest()).setScale(2, RoundingMode.HALF_EVEN)));
		case FxForward(CurrencyPair(var base, var quote), var side, var notional, var forwardRate, var valueDate) -> {
			BigDecimal baseAmount = side.apply(notional);
			yield List.of(new CashFlow(valueDate, base, baseAmount), new CashFlow(valueDate, quote,
					baseAmount.multiply(forwardRate).negate().setScale(2, RoundingMode.HALF_EVEN)));
		}
		case Bond b -> Stream.concat(b.couponDates().stream().map(d -> new CashFlow(d, b.currency(), b.coupon())),
				Stream.of(new CashFlow(b.maturity(), b.currency(), b.faceValue()))).toList();
		};
	}

}
