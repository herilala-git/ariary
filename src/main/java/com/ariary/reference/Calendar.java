package com.ariary.reference;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "calendar")
public class Calendar {

	private static final Set<DayOfWeek> WEEKEND = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

	@Id
	private String code;

	private String name;

	@ElementCollection
	@CollectionTable(name = "holiday", joinColumns = @JoinColumn(name = "calendar_code"))
	@MapKeyColumn(name = "holiday_date")
	@Column(name = "name")
	private Map<LocalDate, String> holidays = new HashMap<>();

	protected Calendar() {
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public Map<LocalDate, String> getHolidays() {
		return Map.copyOf(holidays);
	}

	public void addHoliday(LocalDate day, String holidayName) {
		holidays.put(day, holidayName);
	}

	public boolean isBusinessDay(LocalDate day) {
		return !WEEKEND.contains(day.getDayOfWeek()) && !holidays.containsKey(day);
	}

	public LocalDate nextBusinessDay(LocalDate day) {
		LocalDate next = day.plusDays(1);
		while (!isBusinessDay(next)) {
			next = next.plusDays(1);
		}
		return next;
	}

	public LocalDate previousBusinessDay(LocalDate day) {
		LocalDate previous = day.minusDays(1);
		while (!isBusinessDay(previous)) {
			previous = previous.minusDays(1);
		}
		return previous;
	}

	public LocalDate rollFollowing(LocalDate day) {
		return isBusinessDay(day) ? day : nextBusinessDay(day);
	}

	public LocalDate rollModifiedFollowing(LocalDate day) {
		LocalDate next = rollFollowing(day);
		return next.getMonth() == day.getMonth() ? next : previousBusinessDay(day);
	}

	// TODO jour ouvre precedent
	public List<LocalDate> businessDays(LocalDate from, LocalDate to) {
		return from.datesUntil(to.plusDays(1)).filter(this::isBusinessDay).collect(Collectors.toUnmodifiableList());
	}

}
