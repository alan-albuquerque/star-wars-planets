package com.alantech.starwarsplanets.service.exception;

public class PlanetAlreadyExistsException extends RuntimeException {
	public PlanetAlreadyExistsException() {
		super("Planet with that name already exists!");
	}
}
