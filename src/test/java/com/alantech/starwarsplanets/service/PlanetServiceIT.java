package com.alantech.starwarsplanets.service;

import com.alantech.starwarsplanets.IntegrationTest;
import com.alantech.starwarsplanets.domain.Planet;
import com.alantech.starwarsplanets.dto.PlanetDTO;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@IntegrationTest
class PlanetServiceIT {

	private final PlanetService planetService;

	@Test
	void assertThatPlanetShouldBeCreated() {
		PlanetDTO planetDTO = PlanetDTO.builder()
			.climate("climate 1")
			.name("name 1")
			.terrain("terrain 1")
			.build();
		Planet planet = planetService.create(planetDTO);
		assertThat(planet).isNotNull();
		assertThat(planet.getId()).isNotNull();
	}
}
