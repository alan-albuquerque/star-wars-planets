package com.alantech.starwarsplanets.controller.v1;

import com.alantech.starwarsplanets.IntegrationTest;
import com.alantech.starwarsplanets.common.RestTestUtil;
import com.alantech.starwarsplanets.domain.Planet;
import com.alantech.starwarsplanets.dto.PlanetDTO;
import com.alantech.starwarsplanets.repository.PlanetRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@IntegrationTest
class PlanetControllerIT {

	private final MockMvc mockMvc;
	private final PlanetRepository planetRepository;

	private final static String TERRAIN = "terrain test";
	private final static String NAME = "name test";
	private final static String CLIMATE = "climate test";

	@Test
	void assertThatPlanetWillBeCreated() throws Exception {
		long planetsCountBefore = planetRepository.count();
		PlanetDTO planetDTO = PlanetDTO.builder()
			.terrain(TERRAIN)
			.name(NAME)
			.climate(CLIMATE)
			.build();

		this.mockMvc.perform(
			post("/api/v1/planets/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(RestTestUtil.toJson(planetDTO))
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

}
