package com.ariary.valuation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		LocalDate date = (asOf != null) ? asOf : businessDate.current().getCurrentDay();
		var o = operations.findById(id).orElseThrow(() -> new NoSuchElementException("Operation " + id));
		String currency = o.getCurrency().getCode();
		BigDecimal value = engine.depositValue(o.getNotional(), o.getRate(), o.getDayCount(), o.getStartDate(),
				o.getMaturityDate(), currency, market.load(date));
		return new ValueDto(id, date, currency, value);
	}

}
