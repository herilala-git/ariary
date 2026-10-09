package com.ariary.operation;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ariary.instrument.Side;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OperationDto(Long id, InstrumentType type, Status status, String portfolio, String counterparty,
		String currency, String quoteCurrency, Side side, BigDecimal notional, BigDecimal rate, BigDecimal forwardRate,
		Integer couponFrequency, DayCount dayCount, LocalDate tradeDate, LocalDate startDate, LocalDate maturityDate,
		Long version) {

	public static OperationDto from(Operation o) {
		return new OperationDto(o.getId(), o.getInstrumentType(), o.getStatus(), o.getPortfolio().getCode(),
				o.getCounterparty().getCode(), o.getCurrency().getCode(), o.getQuoteCurrency(), o.getSide(),
				o.getNotional(), o.getRate(), o.getForwardRate(), o.getCouponFrequency(), o.getDayCount(),
				o.getTradeDate(), o.getStartDate(), o.getMaturityDate(), o.getVersion());
	}

}
