package com.ariary.web;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler({ NoSuchElementException.class, IllegalArgumentException.class })
	public ResponseEntity<String> handle(RuntimeException e) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		if (e instanceof NoSuchElementException) {
			status = HttpStatus.NOT_FOUND;
		}
		return ResponseEntity.status(status).body(e.getMessage());
	}

}
