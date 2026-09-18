package com.ariary.reference;

import java.time.LocalDate;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "business_date")
public class BusinessDate {

	@Id
	@JdbcTypeCode(SqlTypes.CHAR) // CHAR(1) cote oracle
	private String id;

	private LocalDate currentDay;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_code")
	private Calendar calendar;

	protected BusinessDate() {
	}

	public LocalDate getCurrentDay() {
		return currentDay;
	}

	public Calendar getCalendar() {
		return calendar;
	}

}
