package com.ariary.reference;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "counterparty")
public class Counterparty {

	@Id
	private String code;

	private String name;

	private String rating;

	protected Counterparty() {
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getRating() {
		return rating;
	}

}
