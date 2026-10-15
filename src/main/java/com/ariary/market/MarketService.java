package com.ariary.market;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MarketService {

	private static final Logger log = LoggerFactory.getLogger(MarketService.class);

	public record ImportResult(LocalDate date, int fxRates, int curvePoints) {
	}

	private final MarketClient client;
	private final FxRateRepository fxRates;
	private final CurvePointRepository curvePoints;

	public MarketService(MarketClient client, FxRateRepository fxRates, CurvePointRepository curvePoints) {
		this.client = client;
		this.fxRates = fxRates;
		this.curvePoints = curvePoints;
	}

	@Transactional
	public ImportResult importDay(LocalDate date) {
		List<MarketClient.FxQuote> fxQuotes;
		List<MarketClient.CurveQuote> curveQuotes;
		try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
			Future<List<MarketClient.FxQuote>> fxCall = executor.submit(() -> client.fxRates(date));
			Future<List<MarketClient.CurveQuote>> curveCall = executor.submit(() -> client.curves(date));
			fxQuotes = waitFor(fxCall);
			curveQuotes = waitFor(curveCall);
		}

		fxRates.deleteByDate(date);
		curvePoints.deleteByDate(date);

		fxRates.saveAll(fxQuotes.stream().map(q -> new FxRate(date, q.base(), q.quote(), q.rate())).toList());
		List<CurvePoint> points = curveQuotes.stream().flatMap(
				curve -> curve.points().stream().map(p -> new CurvePoint(date, curve.currency(), p.days(), p.rate())))
				.toList();
		curvePoints.saveAll(points);
		log.info("market {} imported: {} fx, {} points", date, fxQuotes.size(), points.size());
		return new ImportResult(date, fxQuotes.size(), points.size());
	}

	@Transactional(readOnly = true)
	public MarketData load(LocalDate date) {
		Map<String, BigDecimal> rates = fxRates.findByKeyDate(date).stream().collect(
				Collectors.toMap(r -> r.getKey().baseCurrency() + "/" + r.getKey().quoteCurrency(), FxRate::getRate));
		int count = rates.size();
		Map<String, YieldCurve> curves = curvePoints.findByKeyDateOrderByKeyCurrencyAscKeyDaysAsc(date).stream()
				.collect(Collectors.groupingBy(p -> p.getKey().currency(),
						Collectors.collectingAndThen(Collectors.toList(), MarketService::toCurve)));
		if (rates.isEmpty() && curves.isEmpty()) {
			throw new NoSuchElementException("No market data for " + date);
		}
		return new MarketData(date, rates, curves);
	}

	private static <T> T waitFor(Future<T> call) {
		try {
			return call.get();
		} catch (ExecutionException e) {
			throw (e.getCause() instanceof RuntimeException r) ? r : new IllegalStateException(e.getCause());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("Import interrupted");
		}
	}

	private static YieldCurve toCurve(List<CurvePoint> points) {
		return new YieldCurve(points.getFirst().getKey().currency(), points.stream()
				.map(p -> new YieldCurve.Point(p.getKey().days(), p.getZeroRate().doubleValue())).toList());
	}

}
