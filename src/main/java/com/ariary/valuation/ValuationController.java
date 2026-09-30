package com.ariary.valuation;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ariary.valuation.ValuationService.ValueDto;

@RestController
public class ValuationController {

	private final ValuationService service;

	public ValuationController(ValuationService service) {
		this.service = service;
	}

	@GetMapping("/api/v1/operations/{id}/value")
	public ValueDto value(@PathVariable long id, @RequestParam(required = false) LocalDate asOf) {
		return service.value(id, asOf);
	}

}
