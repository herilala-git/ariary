package com.ariary.reference;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "portfolio")
public class Portfolio {

	@Id
	private String code;

	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "entity_code")
	private LegalEntity entity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_code")
	private Portfolio parent;

	protected Portfolio() {
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public LegalEntity getEntity() {
		return entity;
	}

	public Portfolio getParent() {
		return parent;
	}

}
