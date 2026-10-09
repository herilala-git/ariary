package com.ariary.operation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ariary.operation.OperationService.Summary;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/operations")
public class OperationController {

	public record PageDto(List<OperationDto> content, int page, int size, long total) {
	}

	public record NotionalChange(BigDecimal notional) {
	}

	private final OperationService service;

	public OperationController(OperationService service) {
		this.service = service;
	}

	// creation
	@PostMapping("/deposits")
	@ResponseStatus(HttpStatus.CREATED)
	public OperationDto createDeposit(@Valid @RequestBody NewDepositRequest request) {
		return service.createDeposit(request);
	}

	@PostMapping("/fx-forwards")
	@ResponseStatus(HttpStatus.CREATED)
	public OperationDto createFxForward(@Valid @RequestBody NewFxForwardRequest request) {
		return service.createFxForward(request);
	}

	@PostMapping("/bonds")
	@ResponseStatus(HttpStatus.CREATED)
	public OperationDto createBond(@Valid @RequestBody NewBondRequest request) {
		return service.createBond(request);
	}

	@GetMapping("/{id}")
	public OperationDto get(@PathVariable long id) {
		return service.get(id);
	}

	@GetMapping
	public PageDto search(@RequestParam(required = false) Status status,
			@RequestParam(required = false) String portfolio, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		var result = service.search(status, portfolio, PageRequest.of(page, size, Sort.by("id").descending()));
		return new PageDto(result.getContent(), result.getNumber(), result.getSize(), result.getTotalElements());
	}

	@GetMapping("/summary")
	public Map<String, Summary> summary() {
		return service.summary();
	}

	@PostMapping("/{id}/validate")
	public OperationDto validate(@PathVariable long id) {
		return service.validate(id);
	}

	@PostMapping("/{id}/confirm")
	public OperationDto confirm(@PathVariable long id) {
		return service.confirm(id);
	}

	@PostMapping("/{id}/settle")
	public OperationDto settle(@PathVariable long id) {
		return service.settle(id);
	}

	@PostMapping("/{id}/cancel")
	public OperationDto cancel(@PathVariable long id) {
		return service.cancel(id);
	}

	@PatchMapping("/{id}")
	public OperationDto changeNotional(@PathVariable long id, @RequestBody NotionalChange change) {
		return service.changeNotional(id, change.notional());
	}

}
