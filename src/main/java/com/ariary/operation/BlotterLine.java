package com.ariary.operation;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BlotterLine(Long id, InstrumentType type, Status status, String portfolio, String entity,
		String counterparty, String currency, BigDecimal notional, LocalDate maturity) {
}
