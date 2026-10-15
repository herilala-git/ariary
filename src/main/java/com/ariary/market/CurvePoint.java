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
@Table(name = "curve_point")
public class CurvePoint extends AssignedIdEntity<CurvePoint.Key> {

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

	@Override
	public Key getId() {
		return key;
	}

	public Key getKey() {
		return key;
	}

	public BigDecimal getZeroRate() {
		return zeroRate;
	}

}
