package com.ariary.reference;

import static java.util.function.Predicate.not;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HolidayImporter {

	private static final Logger log = LoggerFactory.getLogger(HolidayImporter.class);

	private final ReferenceService reference;
	private final String dataDir;

	public HolidayImporter(ReferenceService reference, @Value("${ariary.data-dir}") String dataDir) {
		this.reference = reference;
		this.dataDir = dataDir;
	}

	@Transactional
	public String importFile(String code, int year) {
		var calendar = reference.calendar(code);
		var file = Path.of(dataDir, "holidays", calendar.getCode() + "-" + year + ".csv");
		String content;
		try {
			content = Files.readString(file);
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot read " + file);
		}
		List<String[]> lines = content.lines().map(String::strip).filter(not(String::isBlank))
				.filter(line -> !line.startsWith("#"))
				// TODO lignes mal formees (sans ;)
				.map(line -> line.split(";")).toList();
		lines.forEach(parts -> calendar.addHoliday(LocalDate.parse(parts[0]), parts[1]));
		log.info("{} holidays for {} {}", lines.size(), calendar.getCode(), year);
		return lines.size() + " holidays imported into " + calendar.getCode() + " for " + year;
	}

}
