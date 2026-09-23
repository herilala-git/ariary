package com.ariary.operation;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record NewDepositRequest(@NotBlank String portfolio, @NotBlank String counterparty, @NotBlank String currency,
		@NotNull @Positive BigDecimal notional, @NotNull BigDecimal rate, @NotNull DayCount dayCount,
		@NotNull LocalDate startDate, @NotNull LocalDate maturityDate) {
}
