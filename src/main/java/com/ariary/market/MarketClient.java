package com.ariary.market;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class MarketClient {

	public record FxQuote(String base, String quote, BigDecimal rate) {
	}

	public record PointQuote(int days, BigDecimal rate) {
	}

	public record CurveQuote(String currency, List<PointQuote> points) {
	}

	private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
	private final ObjectMapper json;
	private final String url;

	public MarketClient(ObjectMapper json, @Value("${ariary.market.url}") String url) {
		this.json = json;
		this.url = url;
	}

	public List<FxQuote> fxRates(LocalDate date) {
		return read("/market/fx-rates?date=" + date, new TypeReference<>() {
		});
	}

	public List<CurveQuote> curves(LocalDate date) {
		return read("/market/curves?date=" + date, new TypeReference<>() {
		});
	}

	private <T> T read(String path, TypeReference<T> type) {
		var request = HttpRequest.newBuilder(URI.create(url + path)).timeout(Duration.ofSeconds(10))
				.header("Accept", "application/json").GET().build();
		try {
			var response = http.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() != 200) {
				throw new IllegalStateException("Market provider answered " + response.statusCode() + " for " + path);
			}
			return json.readValue(response.body(), type);
		} catch (IOException e) {
			throw new IllegalStateException("Market provider unavailable", e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("Interrupted", e);
		}
	}

}
