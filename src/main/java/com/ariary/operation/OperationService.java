package com.ariary.operation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ariary.reference.BusinessDateRepository;
import com.ariary.reference.Counterparty;
import com.ariary.reference.CounterpartyRepository;
import com.ariary.reference.Currency;
import com.ariary.reference.CurrencyRepository;
import com.ariary.reference.Portfolio;
import com.ariary.reference.PortfolioRepository;

@Service
@Transactional
public class OperationService {

	public record Summary(long count, BigDecimal total) {
	}

	private final OperationRepository operations;
	private final PortfolioRepository portfolios;
	private final CounterpartyRepository counterparties;
	private final CurrencyRepository currencies;
	private final BusinessDateRepository businessDate;

	public OperationService(OperationRepository operations, PortfolioRepository portfolios,
			CounterpartyRepository counterparties, CurrencyRepository currencies, BusinessDateRepository businessDate) {
		this.operations = operations;
		this.portfolios = portfolios;
		this.counterparties = counterparties;
		this.currencies = currencies;
		this.businessDate = businessDate;
	}

	public OperationDto createDeposit(NewDepositRequest r) {
		return create(() -> Operation.deposit(portfolio(r.portfolio()), counterparty(r.counterparty()),
				currency(r.currency()), r.notional(), r.rate(), r.dayCount(), today(), r.startDate(),
				r.maturityDate()));
	}

	public OperationDto createFxForward(NewFxForwardRequest r) {
		return create(() -> Operation.fxForward(portfolio(r.portfolio()), counterparty(r.counterparty()),
				currency(r.baseCurrency()), currency(r.quoteCurrency()), r.side(), r.notional(), r.forwardRate(),
				today(), r.valueDate()));
	}

	public OperationDto createBond(NewBondRequest r) {
		return create(() -> Operation.bond(portfolio(r.portfolio()), counterparty(r.counterparty()),
				currency(r.currency()), r.faceValue(), r.couponRate(), r.couponFrequency(), r.dayCount(), today(),
				r.issueDate(), r.maturity()));
	}

	private OperationDto create(Supplier<Operation> factory) {
		var operation = factory.get();
		operation.instrument();
		return OperationDto.from(operations.save(operation));
	}

	@Transactional(readOnly = true)
	public OperationDto get(long id) {
		// System.out.println("get " + id);
		return OperationDto.from(load(id));
	}

	@Transactional(readOnly = true)
	public Page<OperationDto> search(Status status, String portfolio, Pageable pageable) {
		return operations.search(status, portfolio, pageable).map(OperationDto::from);
	}

	@Transactional(readOnly = true)
	public List<BlotterLine> blotter() {
		return operations.blotter(Status.CANCELLED);
	}

	@Transactional(readOnly = true)
	public Map<String, Summary> summary() {
		return operations.findAll().stream().filter(o -> o.getStatus() != Status.CANCELLED)
				.collect(Collectors.groupingBy(o -> o.getCurrency().getCode(), TreeMap::new,
						Collectors.teeing(Collectors.counting(),
								Collectors.reducing(BigDecimal.ZERO, Operation::getNotional, BigDecimal::add),
								Summary::new)));
	}

	public OperationDto validate(long id) {
		return apply(id, Operation::validate);
	}

	public OperationDto confirm(long id) {
		return apply(id, Operation::confirm);
	}

	public OperationDto settle(long id) {
		return apply(id, Operation::settle);
	}

	public OperationDto cancel(long id) {
		return apply(id, Operation::cancel);
	}

	public OperationDto changeNotional(long id, BigDecimal notional) {
		return apply(id, o -> o.changeNotional(notional));
	}

	private OperationDto apply(long id, Consumer<Operation> change) {
		var operation = load(id);
		change.accept(operation);
		return OperationDto.from(operations.saveAndFlush(operation));
	}

	private Portfolio portfolio(String code) {
		return portfolios.findById(code).orElseThrow(() -> new IllegalArgumentException("Unknown portfolio " + code));
	}

	private Counterparty counterparty(String code) {
		return counterparties.findById(code)
				.orElseThrow(() -> new IllegalArgumentException("Unknown counterparty " + code));
	}

	private Currency currency(String code) {
		return currencies.findById(code).orElseThrow(() -> new IllegalArgumentException("Unknown currency " + code));
	}

	private LocalDate today() {
		return businessDate.current().getCurrentDay();
	}

	private Operation load(long id) {
		return operations.findById(id).orElseThrow(() -> new NoSuchElementException("Operation " + id));
	}

}
