package com.ariary.reference;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReferenceService {

	public record EntityDto(String code, String name, String reportingCurrency) {
	}

	public record CounterpartyDto(String code, String name, String rating) {
	}

	public record Node(String code, String name, String entity, List<Node> children) {
	}

	public record BusinessDateDto(LocalDate date, String calendar, LocalDate nextBusinessDay) {
	}

	public record HolidayDto(LocalDate date, String name) {
	}

	public record RollDto(LocalDate date, boolean businessDay, LocalDate following, LocalDate modifiedFollowing) {
	}

	private static final String ROOT = "";

	private final LegalEntityRepository entities;
	private final PortfolioRepository portfolios;
	private final CounterpartyRepository counterparties;
	private final CalendarRepository calendars;
	private final BusinessDateRepository businessDate;

	public ReferenceService(LegalEntityRepository entities, PortfolioRepository portfolios,
			CounterpartyRepository counterparties, CalendarRepository calendars, BusinessDateRepository businessDate) {
		this.entities = entities;
		this.portfolios = portfolios;
		this.counterparties = counterparties;
		this.calendars = calendars;
		this.businessDate = businessDate;
	}

	public List<EntityDto> entities() {
		return entities.findAll().stream()
				.map(e -> new EntityDto(e.getCode(), e.getName(), e.getReportingCurrency().getCode())).toList();
	}

	public List<CounterpartyDto> counterparties() {
		return counterparties.findAll().stream().map(c -> new CounterpartyDto(c.getCode(), c.getName(), c.getRating()))
				.toList();
	}

	public List<String> portfolioCodes() {
		return portfolios.findAll().stream().map(Portfolio::getCode).toList();
	}

	// arbre des portefeuilles
	public List<Node> portfolioTree() {
		var all = portfolios.findAllWithEntity();
		Map<String, List<Portfolio>> byParent = all.stream()
				.collect(Collectors.groupingBy(p -> p.getParent() == null ? ROOT : p.getParent().getCode()));
		return children(ROOT, byParent);
	}

	private List<Node> children(String parent, Map<String, List<Portfolio>> byParent) {
		return byParent.getOrDefault(parent, List.of()).stream()
				.map(p -> new Node(p.getCode(), p.getName(), p.getEntity().getCode(), children(p.getCode(), byParent)))
				.toList();
	}

	public BusinessDateDto businessDate() {
		var current = businessDate.current();
		var calendar = current.getCalendar();
		return new BusinessDateDto(current.getCurrentDay(), calendar.getCode(),
				calendar.nextBusinessDay(current.getCurrentDay()));
	}

	public List<LocalDate> businessDays(String code, LocalDate from, LocalDate to) {
		return calendar(code).businessDays(from, to);
	}

	public RollDto roll(String code, LocalDate date) {
		var calendar = calendar(code);
		return new RollDto(date, calendar.isBusinessDay(date), calendar.rollFollowing(date),
				calendar.rollModifiedFollowing(date));
	}

	public List<HolidayDto> holidays(String code, int year) {
		return calendar(code).getHolidays().entrySet().stream().filter(h -> h.getKey().getYear() == year)
				.sorted(Map.Entry.comparingByKey()).map(h -> new HolidayDto(h.getKey(), h.getValue())).toList();
	}

	Calendar calendar(String code) {
		return calendars.findById(code.toUpperCase()).orElseThrow();
	}

}
