package com.ariary.instrument;

public record CurrencyPair(String base, String quote) {

	public CurrencyPair {
		if (base.equals(quote)) {
			throw new IllegalArgumentException("Same currency twice: " + base);
		}
	}

}
