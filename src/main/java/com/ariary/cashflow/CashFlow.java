package com.ariary.cashflow;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CashFlow(LocalDate date, String currency, BigDecimal amount) {
}
