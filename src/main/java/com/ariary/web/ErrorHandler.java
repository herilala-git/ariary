package com.ariary.web;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler({ NoSuchElementException.class, IllegalArgumentException.class, IllegalStateException.class })
	public ResponseEntity<String> handle(RuntimeException e) {
		return switch (e) {
		case NoSuchElementException notFound when notFound.getMessage() == null ->
			ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
		case NoSuchElementException notFound -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound.getMessage());
		case IllegalArgumentException badRequest -> ResponseEntity.badRequest().body(badRequest.getMessage());
		default -> ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		};
	}

}
