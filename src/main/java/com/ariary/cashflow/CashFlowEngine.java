package com.ariary.cashflow;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ariary.instrument.Bond;
import com.ariary.instrument.Deposit;
import com.ariary.instrument.FxForward;
import com.ariary.instrument.Instrument;

@Component
public class CashFlowEngine {

	public List<CashFlow> cashFlows(Instrument instrument) {
		if (instrument instanceof Deposit d) {
			BigDecimal repayment = d.notional().add(d.interest()).setScale(2, RoundingMode.HALF_EVEN);
			return List.of(new CashFlow(d.start(), d.currency(), d.notional().negate()),
					new CashFlow(d.maturity(), d.currency(), repayment));
		}
		if (instrument instanceof FxForward f) {
			BigDecimal base = f.side().apply(f.notional());
			BigDecimal quote = base.multiply(f.forwardRate()).negate().setScale(2, RoundingMode.HALF_EVEN);
			return List.of(new CashFlow(f.valueDate(), f.pair().base(), base),
					new CashFlow(f.valueDate(), f.pair().quote(), quote));
		}
		if (instrument instanceof Bond b) {
			List<CashFlow> flows = new ArrayList<>();
			b.couponDates().forEach(d -> flows.add(new CashFlow(d, b.currency(), b.coupon())));
			flows.add(new CashFlow(b.maturity(), b.currency(), b.faceValue()));
			return flows;
		}
		throw new IllegalArgumentException("Unknown instrument " + instrument);
	}

}
