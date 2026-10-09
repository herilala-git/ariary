package com.ariary.instrument;

import java.math.BigDecimal;

public enum Side {

	BUY(BigDecimal.ONE), SELL(BigDecimal.ONE.negate());

	private final BigDecimal sign;

	Side(BigDecimal sign) {
		this.sign = sign;
	}

	public BigDecimal apply(BigDecimal amount) {
		return amount.multiply(sign);
	}

}
