package com.alantech.starwarsplanets.controller.v1;

import java.util.List;
import java.util.UUID;

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

	private final static String SEARCH_BY_NAME_KEY = "name";
	private final static String TERRAIN = "terrain test";
	private final static String CLIMATE = "climate test";

	@Test
	void Should_PlanetBeCreated_When_ReceivesValidContent() throws Exception {
		long planetsCountBefore = planetRepository.count();
		PlanetDTO planetDTO = PlanetDTO.builder()
			.terrain(TERRAIN)
			.name(UUID.randomUUID().toString())
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
		assertThat(testPlanet.getName()).isEqualTo(planetDTO.getName());
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
	void Should_CreateReturnsConflict_When_ReceivesAnExistentPlanet() throws Exception {

		Planet planet = Planet.builder()
			.terrain(TERRAIN)
			.name(UUID.randomUUID().toString())
			.climate(CLIMATE)
			.build();

		planetRepository.save(planet);

		long planetsCountBefore = planetRepository.count();

		this.mockMvc.perform(
			post("/api/v1/planets/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(MapperTestUtil.toJson(planet))
		)
			.andExpect(status().isConflict());

		assertThat(planetRepository.count()).isEqualTo(planetsCountBefore);

	}

	@Test
	void Should_FindByNameReturnNotFound_When_NotExists() throws Exception {
		this.mockMvc.perform(
			get("/api/v1/planets/name/")
				.param(SEARCH_BY_NAME_KEY, "potato123") // <-- nonexistent name
		)
			.andExpect(status().isNotFound());
	}
	@Test
	void Should_FindByNameReturnNotFound_When_ParamIsEmpty() throws Exception {
		this.mockMvc.perform(
			get("/api/v1/planets/name")
				.param(SEARCH_BY_NAME_KEY, "") // <-- parameter can't be empty
		)
			.andExpect(status().isNotFound());
	}
}
