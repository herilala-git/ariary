package com.ariary.valuation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ariary.instrument.Instrument;
import com.ariary.market.MarketService;
import com.ariary.operation.OperationRepository;
import com.ariary.reference.BusinessDateRepository;

@Service
@Transactional(readOnly = true)
public class ValuationService {

	public record ValueDto(long operation, LocalDate asOf, String currency, BigDecimal value) {
	}

	private final OperationRepository operations;
	private final MarketService market;
	private final ValuationEngine engine;
	private final BusinessDateRepository businessDate;

	public ValuationService(OperationRepository operations, MarketService market, ValuationEngine engine,
			BusinessDateRepository businessDate) {
		this.operations = operations;
		this.market = market;
		this.engine = engine;
		this.businessDate = businessDate;
	}

	public ValueDto value(long id, LocalDate asOf) {
		LocalDate date = dateOrToday(asOf);
		var instrument = instrument(id);
		return new ValueDto(id, date, instrument.valueCurrency(), engine.value(instrument, market.load(date)));
	}

	Instrument instrument(long id) {
		return operations.findById(id).orElseThrow(() -> new NoSuchElementException("Operation " + id)).instrument();
	}

	LocalDate dateOrToday(LocalDate asOf) {
		return (asOf != null) ? asOf : businessDate.current().getCurrentDay();
	}

}
