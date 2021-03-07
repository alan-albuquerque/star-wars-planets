package com.alantech.starwarsplanets.service;

import com.alantech.starwarsplanets.network.swapi.model.SwapiPlanet;

public interface SwapiService {
	SwapiPlanet getPlanetByName(String name);
}
