package com.ariary.valuation;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ariary.valuation.StressService.StressDto;
import com.ariary.valuation.ValuationService.ValueDto;

@RestController
public class ValuationController {

	private final ValuationService service;
	private final StressService stress;

	public ValuationController(ValuationService service, StressService stress) {
		this.service = service;
		this.stress = stress;
	}

	@GetMapping("/api/v1/operations/{id}/value")
	public ValueDto value(@PathVariable long id, @RequestParam(required = false) LocalDate asOf) {
		return service.value(id, asOf);
	}

	@GetMapping("/api/v1/operations/{id}/stress")
	public StressDto stress(@PathVariable long id, @RequestParam(required = false) LocalDate asOf,
			@RequestParam(defaultValue = "1000") int scenarios, @RequestParam(defaultValue = "50") double volatilityBps,
			@RequestParam(defaultValue = "1") long seed) {
		return stress.stress(id, asOf, scenarios, volatilityBps, seed);
	}

}
