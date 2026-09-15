package com.ariary.reference;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "currency")
public class Currency {

	@Id
	private String code;

	private String name;

	protected Currency() {
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

}
