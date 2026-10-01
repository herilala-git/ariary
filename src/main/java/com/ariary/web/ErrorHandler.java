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
		if (e instanceof NoSuchElementException notFound) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(notFound.getMessage() == null ? "Not found" : notFound.getMessage());
		}
		HttpStatus status = (e instanceof IllegalArgumentException) ? HttpStatus.BAD_REQUEST : HttpStatus.CONFLICT;
		return ResponseEntity.status(status).body(e.getMessage());
	}

}
