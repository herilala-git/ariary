package com.ariary.market;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.ariary.common.AssignedIdEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "fx_rate")
public class FxRate extends AssignedIdEntity<FxRate.Key> {

	@Embeddable
	public record Key(@Column(name = "rate_date") LocalDate date, String baseCurrency, String quoteCurrency)
			implements Serializable {
	}

	@EmbeddedId
	private Key key;

	private BigDecimal rate;

	protected FxRate() {
	}

	public FxRate(LocalDate date, String base, String quote, BigDecimal rate) {
		this.key = new Key(date, base, quote);
		this.rate = rate;
	}

	@Override
	public Key getId() {
		return key;
	}

	public Key getKey() {
		return key;
	}

	public BigDecimal getRate() {
		return rate;
	}

}
