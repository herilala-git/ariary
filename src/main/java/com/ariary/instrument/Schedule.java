package com.ariary.instrument;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public final class Schedule {

	private Schedule() {
	}

	public static List<LocalDate> dates(LocalDate issue, LocalDate maturity, int frequency) {
		int months = 12 / frequency;
		// toujours depuis la date d'emission, sinon 31/01 -> 28/02 -> 28/03
		return Stream.iterate(1, k -> k + 1).map(k -> issue.plusMonths((long) k * months))
				.takeWhile(d -> !d.isAfter(maturity)).toList();
	}

}
