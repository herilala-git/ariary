package com.ariary.valuation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.ariary.market.MarketService;

@Service
public class StressService {

	public record StressDto(long operation, LocalDate asOf, int scenarios, double volatilityBps, BigDecimal baseValue,
			BigDecimal worst5Percent, BigDecimal average, BigDecimal best) {
	}

	private final ValuationService valuation;
	private final ValuationEngine engine;
	private final MarketService market;

	public StressService(ValuationService valuation, ValuationEngine engine, MarketService market) {
		this.valuation = valuation;
		this.engine = engine;
		this.market = market;
	}

	public StressDto stress(long id, LocalDate asOf, int scenarios, double volatilityBps, long seed) {
		LocalDate date = valuation.dateOrToday(asOf);
		var instrument = valuation.instrument(id);
		var marketData = market.load(date);
		// RandomGenerator random = RandomGenerator.getDefault();
		// meme seed = memes resultats
		RandomGenerator random = new Random(seed);

		List<BigDecimal> values = Stream.generate(() -> random.nextGaussian(0, volatilityBps)).limit(scenarios)
				.map(shock -> engine.value(instrument, marketData.shift(shock))).sorted().toList();

		BigDecimal sum = values.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
		return new StressDto(id, date, scenarios, volatilityBps, engine.value(instrument, marketData),
				values.get(scenarios / 20), sum.divide(BigDecimal.valueOf(scenarios), 2, RoundingMode.HALF_EVEN),
				values.get(scenarios - 1));
	}

}
