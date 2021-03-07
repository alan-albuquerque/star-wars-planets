package com.alantech.starwarsplanets.exception;

public class SwapiInvalidResponseException extends RuntimeException {

	public SwapiInvalidResponseException(String message) {
		super(message);
	}

	public SwapiInvalidResponseException(String message, Throwable e) {
		super(message, e);
	}

}
