package com.ariary.cashflow;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ariary.operation.OperationRepository;

@RestController
public class CashFlowController {

	private final OperationRepository operations;
	private final CashFlowEngine engine;

	public CashFlowController(OperationRepository operations, CashFlowEngine engine) {
		this.operations = operations;
		this.engine = engine;
	}

	@GetMapping("/api/v1/operations/{id}/cashflows")
	@Transactional(readOnly = true)
	public List<CashFlow> cashFlows(@PathVariable long id) {
		var operation = operations.findById(id).orElseThrow(() -> new NoSuchElementException("Operation " + id));
		return engine.cashFlows(operation.instrument());
	}

}
