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
		HttpStatus status = HttpStatus.CONFLICT;
		if (e instanceof NoSuchElementException) {
			status = HttpStatus.NOT_FOUND;
		} else if (e instanceof IllegalArgumentException) {
			status = HttpStatus.BAD_REQUEST;
		}
		return ResponseEntity.status(status).body(e.getMessage());
	}

}
