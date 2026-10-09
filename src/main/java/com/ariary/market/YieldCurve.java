package com.ariary.market;

import java.util.List;

public record YieldCurve(String currency, List<Point> points) {

	public record Point(int days, double rate) {
	}

	public YieldCurve {
		if (points.isEmpty()) {
			throw new IllegalArgumentException("Curve " + currency + " has no point");
		}
		points = List.copyOf(points);
		for (int i = 1; i < points.size(); i++) {
			if (points.get(i).days() <= points.get(i - 1).days()) {
				throw new IllegalArgumentException("Curve " + currency + ": days must increase");
			}
		}
	}

	public double zeroRate(long days) {
		Point first = points.get(0);
		if (days <= first.days()) {
			return first.rate();
		}
		for (int i = 1; i < points.size(); i++) {
			Point a = points.get(i - 1);
			Point b = points.get(i);
			if (days <= b.days()) {
				double weight = (double) (days - a.days()) / (b.days() - a.days());
				return a.rate() + weight * (b.rate() - a.rate());
			}
		}
		// taux plat apres le dernier point
		return points.get(points.size() - 1).rate();
	}

	public YieldCurve shift(double basisPoints) {
		return new YieldCurve(currency,
				points.stream().map(p -> new Point(p.days(), p.rate() + basisPoints / 10_000)).toList());
	}

	public double discountFactor(long days) {
		return Math.exp(-zeroRate(days) * days / 365.0);
	}

}
