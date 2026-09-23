package com.ariary.operation;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OperationDto(Long id, InstrumentType type, Status status, String portfolio, String counterparty,
		String currency, BigDecimal notional, BigDecimal rate, DayCount dayCount, LocalDate tradeDate,
		LocalDate startDate, LocalDate maturityDate, Long version) {

	public static OperationDto from(Operation o) {
		return new OperationDto(o.getId(), o.getInstrumentType(), o.getStatus(), o.getPortfolio().getCode(),
				o.getCounterparty().getCode(), o.getCurrency().getCode(), o.getNotional(), o.getRate(), o.getDayCount(),
				o.getTradeDate(), o.getStartDate(), o.getMaturityDate(), o.getVersion());
	}

}
