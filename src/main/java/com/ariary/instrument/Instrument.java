package com.ariary.instrument;

import java.math.BigDecimal;
import java.time.LocalDate;

public sealed interface Instrument permits Deposit, FxForward, Bond {

	String currency();

	BigDecimal notional();

	String valueCurrency();

	LocalDate maturity();

}
