package com.ariary.operation;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// TODO factoriser portfolio/counterparty avec les autres requetes
public record NewBondRequest(
		@NotBlank String portfolio,
		@NotBlank String counterparty,
		@NotBlank String currency,
		@NotNull @Positive BigDecimal faceValue,
		@NotNull BigDecimal couponRate,
		@NotNull Integer couponFrequency,
		@NotNull DayCount dayCount,
		@NotNull LocalDate issueDate,
		@NotNull LocalDate maturity) {
}
