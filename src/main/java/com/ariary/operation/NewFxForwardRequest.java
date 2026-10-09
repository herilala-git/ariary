package com.ariary.operation;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ariary.instrument.Side;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record NewFxForwardRequest(
		@NotBlank String portfolio,
		@NotBlank String counterparty,
		@NotBlank String baseCurrency,
		@NotBlank String quoteCurrency,
		@NotNull Side side,
		@NotNull @Positive BigDecimal notional,
		@NotNull @Positive BigDecimal forwardRate,
		@NotNull LocalDate valueDate) {
}
