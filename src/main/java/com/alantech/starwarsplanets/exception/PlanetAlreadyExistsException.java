package com.alantech.starwarsplanets.exception;

public class PlanetAlreadyExistsException extends RuntimeException {
	public PlanetAlreadyExistsException() {
		super("Planet with that name already exists!");
	}
}
