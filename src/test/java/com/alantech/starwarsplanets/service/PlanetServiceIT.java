package com.alantech.starwarsplanets.service;

import java.util.UUID;

import com.alantech.starwarsplanets.IntegrationTest;
import com.alantech.starwarsplanets.domain.Planet;
import com.alantech.starwarsplanets.dto.CreatePlanetDTO;
import com.alantech.starwarsplanets.service.exception.PlanetAlreadyExistsException;
import com.alantech.starwarsplanets.service.impl.PlanetServiceImpl;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@IntegrationTest
class PlanetServiceIT {

	private final PlanetServiceImpl planetService;

	@Test
	void assertThatPlanetShouldBeCreated() {
		CreatePlanetDTO planetDTO = CreatePlanetDTO.builder()
			.climate("climate 1")
			.name(UUID.randomUUID().toString())
			.terrain("terrain 1")
			.build();
		Planet planet = planetService.create(planetDTO);
		assertThat(planet).isNotNull();
		assertThat(planet.getId()).isNotNull();
	}

	@Test
	void assertThatPlanetShouldNotBeCreatedIfAlreadyExists() {
		CreatePlanetDTO planetDTO = CreatePlanetDTO.builder()
			.climate("climate 1")
			.name(UUID.randomUUID().toString())
			.terrain("terrain 1")
			.build();
		Planet planet = planetService.create(planetDTO);

		assertThat(planet).isNotNull();
		assertThat(planet.getId()).isNotNull();
		Throwable exception = assertThrows(
			PlanetAlreadyExistsException.class,
			() -> planetService.create(planetDTO)
		);
		assertThat(exception.getMessage()).isEqualTo("Planet with that name already exists!");

	}
}
