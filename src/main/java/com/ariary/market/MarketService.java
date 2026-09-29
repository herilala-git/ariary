package com.ariary.market;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
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

	// cache simple : le marche d'une date ne change pas
	private final Map<LocalDate, MarketData> cache = new ConcurrentHashMap<>();
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
		var fxQuotes = client.fxRates(date);
		var curveQuotes = client.curves(date);

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
		MarketData cached = cache.get(date);
		if (cached != null) {
			return cached;
		}
		Map<String, BigDecimal> rates = fxRates.findByKeyDate(date).stream().collect(
				Collectors.toMap(r -> r.getKey().baseCurrency() + "/" + r.getKey().quoteCurrency(), FxRate::getRate));
		int count = rates.size();
		Map<String, YieldCurve> curves = curvePoints.findByKeyDateOrderByKeyCurrencyAscKeyDaysAsc(date).stream()
				.collect(Collectors.groupingBy(p -> p.getKey().currency(),
						Collectors.collectingAndThen(Collectors.toList(), MarketService::toCurve)));
		if (rates.isEmpty() && curves.isEmpty()) {
			throw new NoSuchElementException("No market data for " + date);
		}
		var data = new MarketData(date, rates, curves);
		cache.put(date, data);
		return data;
	}

	private static YieldCurve toCurve(List<CurvePoint> points) {
		return new YieldCurve(points.get(0).getKey().currency(), points.stream()
				.map(p -> new YieldCurve.Point(p.getKey().days(), p.getZeroRate().doubleValue())).toList());
	}

}
