package com.alantech.starwarsplanets.controller.v1;

import java.util.List;
import java.util.Optional;

import com.alantech.starwarsplanets.IntegrationTest;
import com.alantech.starwarsplanets.common.MapperTestUtil;
import com.alantech.starwarsplanets.domain.Planet;
import com.alantech.starwarsplanets.dto.PlanetDTO;
import com.alantech.starwarsplanets.repository.PlanetRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@IntegrationTest
class PlanetControllerIT {

	private final MockMvc mockMvc;
	private final PlanetRepository planetRepository;

	private final static String SEARCH_QUERY_KEY = "query";
	private final static String TERRAIN = "terrain test";
	private final static String NAME = "name test";
	private final static String NAME_SEARCH_UNIQUE_ITEM = "name search unique item test";
	private final static String NAME_SEARCH_UNIQUE_ITEM_TWO = "name search unique item test two";
	private final static String CLIMATE = "climate test";

	@Test
	void Should_PlanetBeCreated_When_ReceivesValidContent() throws Exception {
		long planetsCountBefore = planetRepository.count();
		PlanetDTO planetDTO = PlanetDTO.builder()
			.terrain(TERRAIN)
			.name(NAME)
			.climate(CLIMATE)
			.build();

		this.mockMvc.perform(
			post("/api/v1/planets/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(MapperTestUtil.toJson(planetDTO))
		)
			.andExpect(status().isCreated());

		List<Planet> planets = planetRepository.findAll();
		assertThat(planets).hasSize((int) planetsCountBefore + 1);
		Planet testPlanet = planets.get(planets.size() - 1);
		assertThat(testPlanet.getName()).isEqualTo(NAME);
		assertThat(testPlanet.getClimate()).isEqualTo(CLIMATE);
		assertThat(testPlanet.getTerrain()).isEqualTo(TERRAIN);
		assertThat(testPlanet.getId()).isNotEmpty();
	}

	@Test
	void Should_ReturnValidationError_When_ReceivesInvalidContent() throws Exception {
		long planetsCountBefore = planetRepository.count();

		// we'll send a empty planet, but some fields are required, it must fail with a bad request as response.
		PlanetDTO planetDTO = PlanetDTO.builder().build();

		this.mockMvc.perform(
			post("/api/v1/planets/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(MapperTestUtil.toJson(planetDTO))
		)
			.andExpect(status().isBadRequest());

		assertThat(planetRepository.count()).isEqualTo(planetsCountBefore);

	}

	@Test
	void Should_SearchFoundACreatedPlanet_When_Exists() throws Exception {
		Planet planet = Planet.builder()
			.terrain(TERRAIN)
			.name(NAME_SEARCH_UNIQUE_ITEM)
			.climate(CLIMATE)
			.build();

		planetRepository.deleteAll();
		planetRepository.save(planet);

		MvcResult result = this.mockMvc.perform(
			get("/api/v1/planets/search")
				.param(SEARCH_QUERY_KEY, NAME_SEARCH_UNIQUE_ITEM)
		)
			.andExpect(status().isOk())
			.andReturn();

		List<Planet> foundedPlanets = MapperTestUtil.toObjectList(result.getResponse().getContentAsString(), Planet.class);
		Optional<Planet> foundedPlanet = foundedPlanets
			.stream()
			.filter(item -> item.getName().equals(NAME_SEARCH_UNIQUE_ITEM))
			.findFirst();

		assertThat(foundedPlanet).isPresent();
		assertThat(foundedPlanets).hasSize(1);
	}

	@Test
	void Should_SearchFoundMultipleCreatedPlanets_When_Exists() throws Exception {
		Planet planet = Planet.builder()
			.terrain(TERRAIN)
			.name(NAME_SEARCH_UNIQUE_ITEM)
			.climate(CLIMATE)
			.build();

		Planet planet2 = Planet.builder()
			.terrain(TERRAIN)
			.name(NAME_SEARCH_UNIQUE_ITEM_TWO)
			.climate(CLIMATE)
			.build();

		planetRepository.deleteAll();
		planetRepository.save(planet);
		planetRepository.save(planet2);

		MvcResult result = this.mockMvc.perform(
			get("/api/v1/planets/search")
				.param(SEARCH_QUERY_KEY, NAME_SEARCH_UNIQUE_ITEM) // this endpoint matches with "starts with" algorithm
		)
			.andExpect(status().isOk())
			.andReturn();

		List<Planet> foundedPlanets = MapperTestUtil.toObjectList(result.getResponse().getContentAsString(), Planet.class);
		Optional<Planet> foundedPlanet = foundedPlanets
			.stream()
			.filter(item -> item.getName().equals(NAME_SEARCH_UNIQUE_ITEM))
			.findFirst();
		Optional<Planet> foundedPlanet2 = foundedPlanets
			.stream()
			.filter(item -> item.getName().equals(NAME_SEARCH_UNIQUE_ITEM_TWO))
			.findFirst();
		assertThat(foundedPlanet).isPresent();
		assertThat(foundedPlanet2).isPresent();
		assertThat(foundedPlanets).hasSize(2);
	}

	@Test
	void Should_SearchReturnNotFound_When_NotExists() throws Exception {
		this.mockMvc.perform(
			get("/api/v1/planets/search")
				.param(SEARCH_QUERY_KEY, "potato123") // <-- nonexistent name
		)
			.andExpect(status().isNotFound());
	}
	@Test
	void Should_SearchReturnConstraintViolationException_When_Invalid() throws Exception {
		this.mockMvc.perform(
			get("/api/v1/planets/search")
				.param(SEARCH_QUERY_KEY, "") // <-- query can't be empty
		)
			.andExpect(status().isBadRequest());
	}
}
