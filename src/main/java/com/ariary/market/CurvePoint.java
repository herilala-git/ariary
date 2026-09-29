package com.ariary.market;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "curve_point")
public class CurvePoint {

	@Embeddable
	public record Key(@Column(name = "curve_date") LocalDate date, String currency, int days) implements Serializable {
	}

	@EmbeddedId
	private Key key;

	private BigDecimal zeroRate;

	protected CurvePoint() {
	}

	public CurvePoint(LocalDate date, String currency, int days, BigDecimal zeroRate) {
		this.key = new Key(date, currency, days);
		this.zeroRate = zeroRate;
	}

	public Key getKey() {
		return key;
	}

	public BigDecimal getZeroRate() {
		return zeroRate;
	}

}
