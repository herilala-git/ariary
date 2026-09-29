package com.ariary.reference;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/currencies")
public class CurrencyController {

	public record CurrencyDto(String code, String name) {

		static CurrencyDto from(Currency c) {
			return new CurrencyDto(c.getCode(), c.getName());
		}

	}

	private final CurrencyRepository repository;

	public CurrencyController(CurrencyRepository repository) {
		this.repository = repository;
	}

	@GetMapping
	public List<CurrencyDto> list() {
		return repository.findAllByOrderByCodeAsc().stream().map(CurrencyDto::from).toList();
	}

	@GetMapping("/{code}")
	public ResponseEntity<CurrencyDto> get(@PathVariable String code) {
		return ResponseEntity.of(repository.findById(code.toUpperCase()).map(CurrencyDto::from));
	}

}
