package com.alantech.starwarsplanets.network.swapi;

import com.alantech.starwarsplanets.network.swapi.model.ResultsResponse;
import com.alantech.starwarsplanets.network.swapi.model.SwapiPlanet;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import org.springframework.stereotype.Service;

@Service
public interface SwapiClient {
	@GET("planets")
	Call<ResultsResponse<SwapiPlanet>> searchPlanetByName(@Query("search") String planetName);
}
