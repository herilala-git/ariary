package com.ariary.market;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ariary.market.MarketService.ImportResult;

@RestController
@RequestMapping("/api/v1/market")
public class MarketController {

	public record RateDto(String currency, LocalDate date, long days, double zeroRate, double discountFactor) {
	}

	private final MarketService service;

	public MarketController(MarketService service) {
		this.service = service;
	}

	@PostMapping("/import")
	public ImportResult importDay(@RequestParam LocalDate date) {
		return service.importDay(date);
	}

	@GetMapping("/curves/{currency}")
	public RateDto rate(@PathVariable String currency, @RequestParam LocalDate date, @RequestParam long days) {
		var curve = service.load(date).curve(currency.toUpperCase());
		return new RateDto(curve.currency(), date, days, curve.zeroRate(days), curve.discountFactor(days));
	}

}
