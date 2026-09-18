package com.ariary.reference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "legal_entity")
public class LegalEntity {

	@Id
	private String code;

	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporting_currency")
	private Currency reportingCurrency;

	protected LegalEntity() {
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public Currency getReportingCurrency() {
		return reportingCurrency;
	}

}
