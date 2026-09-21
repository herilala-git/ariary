package com.ariary.reference;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ariary.reference.ReferenceService.BusinessDateDto;
import com.ariary.reference.ReferenceService.CounterpartyDto;
import com.ariary.reference.ReferenceService.EntityDto;
import com.ariary.reference.ReferenceService.HolidayDto;
import com.ariary.reference.ReferenceService.Node;
import com.ariary.reference.ReferenceService.RollDto;

@RestController
@RequestMapping("/api/v1")
public class ReferenceController {

	private final ReferenceService service;
	private final HolidayImporter importer;

	public ReferenceController(ReferenceService service, HolidayImporter importer) {
		this.service = service;
		this.importer = importer;
	}

	@GetMapping("/entities")
	public List<EntityDto> entities() {
		return service.entities();
	}

	@GetMapping("/counterparties")
	public List<CounterpartyDto> counterparties() {
		return service.counterparties();
	}

	@GetMapping("/portfolios/tree")
	public List<Node> portfolioTree() {
		return service.portfolioTree();
	}

	@GetMapping("/business-date")
	public BusinessDateDto businessDate() {
		return service.businessDate();
	}

	@GetMapping("/calendars/{code}/business-days")
	public List<LocalDate> businessDays(@PathVariable String code, @RequestParam LocalDate from,
			@RequestParam LocalDate to) {
		return service.businessDays(code, from, to);
	}

	@GetMapping("/calendars/{code}/holidays")
	public List<HolidayDto> holidays(@PathVariable String code, @RequestParam int year) {
		return service.holidays(code, year);
	}

	@GetMapping("/calendars/{code}/roll")
	public RollDto roll(@PathVariable String code, @RequestParam LocalDate date) {
		return service.roll(code, date);
	}

	@PostMapping("/calendars/{code}/holidays/import")
	public String importHolidays(@PathVariable String code, @RequestParam int year) {
		return importer.importFile(code, year);
	}

}
