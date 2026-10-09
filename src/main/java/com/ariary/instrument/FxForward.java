package com.ariary.instrument;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FxForward(CurrencyPair pair, Side side, BigDecimal notional, BigDecimal forwardRate, LocalDate valueDate)
		implements Instrument {

	@Override
	public String currency() {
		return pair.base();
	}

	@Override
	public String valueCurrency() {
		return pair.quote();
	}

	@Override
	public LocalDate maturity() {
		return valueDate;
	}

}
