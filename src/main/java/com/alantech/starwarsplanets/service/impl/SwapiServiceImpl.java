package com.alantech.starwarsplanets.service.impl;

import java.io.IOException;

import com.alantech.starwarsplanets.config.AppProperties;
import com.alantech.starwarsplanets.exception.SwapResourceNotFoundException;
import com.alantech.starwarsplanets.exception.SwapiInvalidResponseException;
import com.alantech.starwarsplanets.network.swapi.SwapiClient;
import com.alantech.starwarsplanets.network.swapi.model.ResultsResponse;
import com.alantech.starwarsplanets.network.swapi.model.SwapiPlanet;
import com.alantech.starwarsplanets.service.SwapiService;
import org.springframework.context.annotation.Profile;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Profile("!test")
public class SwapiServiceImpl implements SwapiService {
	SwapiClient swapiClient;

	@Autowired
	SwapiServiceImpl(AppProperties appProperties) {
		Retrofit retrofit = new Retrofit.Builder()
			.baseUrl(appProperties.getStarWarsApi().getUrl())
			.addConverterFactory(JacksonConverterFactory.create())
			.build();
		this.swapiClient = retrofit.create(SwapiClient.class);
	}

	public SwapiPlanet getPlanetByName(String name) {
		ResultsResponse<SwapiPlanet> response;
		try {
			response = this.swapiClient.searchPlanetByName(name).execute().body();
		}
		catch (IOException e) {
			throw new SwapiInvalidResponseException("Swapi returned an invalid body response");
		}
		if (response == null) {
			throw new SwapiInvalidResponseException("Swapi returned an invalid empty response");
		}
		return response
			.getResults()
			.stream()
			.filter(swapiPlanet -> swapiPlanet.getName().equals(name))
			.findFirst()
			.orElseThrow(() -> new SwapResourceNotFoundException("Planet not found on Swapi."));
	}

}
