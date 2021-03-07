package com.alantech.starwarsplanets.service.impl;

import com.alantech.starwarsplanets.network.swapi.model.SwapiPlanet;
import com.alantech.starwarsplanets.service.SwapiService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class TestSwapiServiceImpl implements SwapiService {

	public SwapiPlanet getPlanetByName(String name) {
		return SwapiPlanet.builder()
			.name(name)
			.build();
	}

}
