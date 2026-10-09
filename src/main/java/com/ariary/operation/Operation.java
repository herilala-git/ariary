package com.ariary.operation;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import com.ariary.instrument.Bond;
import com.ariary.instrument.CurrencyPair;
import com.ariary.instrument.Deposit;
import com.ariary.instrument.FxForward;
import com.ariary.instrument.Instrument;
import com.ariary.instrument.Side;
import com.ariary.reference.Counterparty;
import com.ariary.reference.Currency;
import com.ariary.reference.Portfolio;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "operation")
public class Operation {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operation_seq")
	@SequenceGenerator(name = "operation_seq", sequenceName = "operation_seq", allocationSize = 1)
	private Long id;

	@Enumerated(EnumType.STRING)
	private InstrumentType instrumentType;

	@Enumerated(EnumType.STRING)
	private Status status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "portfolio_code")
	private Portfolio portfolio;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "counterparty_code")
	private Counterparty counterparty;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "currency")
	private Currency currency;

	private BigDecimal notional;

	private BigDecimal rate;

	@Enumerated(EnumType.STRING)
	private DayCount dayCount;

	private LocalDate tradeDate;

	private LocalDate startDate;

	private LocalDate maturityDate;

	private String quoteCurrency;

	@Enumerated(EnumType.STRING)
	private Side side;

	private BigDecimal forwardRate;

	private Integer couponFrequency;

	@Version
	private Long version;

	private Instant createdAt;

	protected Operation() {
	}

	private static Operation create(InstrumentType type, Portfolio portfolio, Counterparty counterparty,
			Currency currency, BigDecimal notional, LocalDate tradeDate, LocalDate startDate, LocalDate maturityDate) {
		var o = new Operation();
		o.instrumentType = type;
		o.status = Status.NEW;
		o.portfolio = portfolio;
		o.counterparty = counterparty;
		o.currency = currency;
		o.notional = notional;
		o.tradeDate = tradeDate;
		o.startDate = startDate;
		o.maturityDate = maturityDate;
		o.createdAt = Instant.now();
		return o;
	}

	public static Operation deposit(Portfolio portfolio, Counterparty counterparty, Currency currency,
			BigDecimal notional, BigDecimal rate, DayCount dayCount, LocalDate tradeDate, LocalDate startDate,
			LocalDate maturityDate) {
		var o = create(InstrumentType.DEPOSIT, portfolio, counterparty, currency, notional, tradeDate, startDate,
				maturityDate);
		o.rate = rate;
		o.dayCount = dayCount;
		return o;
	}

	public static Operation fxForward(Portfolio portfolio, Counterparty counterparty, Currency base, Currency quote,
			Side side, BigDecimal notional, BigDecimal forwardRate, LocalDate tradeDate, LocalDate valueDate) {
		var o = create(InstrumentType.FX_FORWARD, portfolio, counterparty, base, notional, tradeDate, tradeDate,
				valueDate);
		o.quoteCurrency = quote.getCode();
		o.side = side;
		o.forwardRate = forwardRate;
		return o;
	}

	public static Operation bond(Portfolio portfolio, Counterparty counterparty, Currency currency,
			BigDecimal faceValue, BigDecimal couponRate, int couponFrequency, DayCount dayCount, LocalDate tradeDate,
			LocalDate issueDate, LocalDate maturity) {
		var o = create(InstrumentType.BOND, portfolio, counterparty, currency, faceValue, tradeDate, issueDate,
				maturity);
		o.rate = couponRate;
		o.couponFrequency = couponFrequency;
		o.dayCount = dayCount;
		return o;
	}

	public Instrument instrument() {
		String code = currency.getCode();
		return switch (instrumentType) {
		case DEPOSIT -> new Deposit(code, notional, rate, dayCount, startDate, maturityDate);
		case FX_FORWARD ->
			new FxForward(new CurrencyPair(code, quoteCurrency), side, notional, forwardRate, maturityDate);
		case BOND -> new Bond(code, notional, rate, couponFrequency, dayCount, startDate, maturityDate);
		};
	}

	public void validate() {
		status = status.moveTo(Status.VALIDATED);
	}

	public void confirm() {
		status = status.moveTo(Status.CONFIRMED);
	}

	public void settle() {
		status = status.moveTo(Status.SETTLED);
	}

	public void cancel() {
		status = status.moveTo(Status.CANCELLED);
	}

	public void changeNotional(BigDecimal newNotional) {
		if (status != Status.NEW) {
			throw new IllegalStateException("Cannot change a " + status + " operation");
		}
		notional = newNotional;
	}

	public Long getId() {
		return id;
	}

	public InstrumentType getInstrumentType() {
		return instrumentType;
	}

	public Status getStatus() {
		return status;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public Counterparty getCounterparty() {
		return counterparty;
	}

	public Currency getCurrency() {
		return currency;
	}

	public BigDecimal getNotional() {
		return notional;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public DayCount getDayCount() {
		return dayCount;
	}

	public LocalDate getTradeDate() {
		return tradeDate;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getMaturityDate() {
		return maturityDate;
	}

	public String getQuoteCurrency() {
		return quoteCurrency;
	}

	public Side getSide() {
		return side;
	}

	public BigDecimal getForwardRate() {
		return forwardRate;
	}

	public Integer getCouponFrequency() {
		return couponFrequency;
	}

	public Long getVersion() {
		return version;
	}

}
