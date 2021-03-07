package com.alantech.starwarsplanets.controller.v1;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.alantech.starwarsplanets.IntegrationTest;
import com.alantech.starwarsplanets.domain.Planet;
import com.alantech.starwarsplanets.dto.CreatePlanetDTO;
import com.alantech.starwarsplanets.repository.PlanetRepository;
import com.alantech.starwarsplanets.testUtils.RestResponsePage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@IntegrationTest
class PlanetControllerIT {

	private final MockMvc mockMvc;
	private final PlanetRepository planetRepository;
	private final ObjectMapper objectMapper;

	private final static String TERRAIN = "terrain test";
	private final static String CLIMATE = "climate test";

	@Test
	void Should_PlanetBeCreated_When_ReceivesValidContent() throws Exception {
		long planetsCountBefore = planetRepository.count();
		CreatePlanetDTO planetDTO = CreatePlanetDTO.builder()
			.terrain(TERRAIN)
			.name(UUID.randomUUID().toString())
			.climate(CLIMATE)
			.build();

		this.mockMvc.perform(
			post("/api/v1/planets/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(planetDTO))
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
		CreatePlanetDTO planetDTO = CreatePlanetDTO.builder().build();

		this.mockMvc.perform(
			post("/api/v1/planets/")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(planetDTO))
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
				.content(objectMapper.writeValueAsString(planet))
		)
			.andExpect(status().isConflict());

		assertThat(planetRepository.count()).isEqualTo(planetsCountBefore);

	}

	@Test
	void Should_ListReturnPlanets_When_Exists() throws Exception {

		Planet planet1 = Planet.builder()
			.terrain(TERRAIN)
			.name(UUID.randomUUID().toString())
			.climate(CLIMATE)
			.build();
		Planet planet2 = Planet.builder()
			.terrain(TERRAIN)
			.name(UUID.randomUUID().toString())
			.climate(CLIMATE)
			.build();

		planetRepository.deleteAll();
		planetRepository.save(planet1);
		planetRepository.save(planet2);

		MvcResult mvcResult = this.mockMvc.perform(
			get("/api/v1/planets/")
		)
			.andExpect(status().isOk()).andReturn();

		Page<Planet> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<RestResponsePage<Planet>>() {});

		Planet firstPlanet = response.getContent().stream().filter(planet -> planet.getName().equals(planet1.getName())).findFirst().get();
		Planet secondPlanet = response.getContent().stream().filter(planet -> planet.getName().equals(planet2.getName())).findFirst().get();
		assertThat(firstPlanet).isEqualTo(planet1);
		assertThat(secondPlanet).isEqualTo(planet2);
		assertThat(response.getTotalPages()).isEqualTo(1);
		assertThat(response.getTotalElements()).isEqualTo(2);

	}

	@Test
	void Should_ListReturnEmptyList_When_WithoutPlanets() throws Exception {

		planetRepository.deleteAll();

		MvcResult mvcResult = this.mockMvc.perform(
			get("/api/v1/planets/")
		)
			.andExpect(status().isOk()).andReturn();

		Page<Planet> response = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<RestResponsePage<Planet>>() {});

		List<Planet> planets = response.getContent();
		assertThat(planets).isEmpty();
		assertThat(response.getTotalPages()).isZero();
		assertThat(response.getTotalElements()).isZero();

	}
	@Test
	void Should_FindByNameReturnPlanet_When_Exists() throws Exception {

		Planet planet = Planet.builder()
			.terrain(TERRAIN)
			.name(UUID.randomUUID().toString())
			.climate(CLIMATE)
			.build();

		planetRepository.save(planet);

		MvcResult mvcResult = this.mockMvc.perform(
			get("/api/v1/planets/name/{name}", planet.getName())
		)
			.andExpect(status().isOk()).andReturn();

		Planet foundedPlanet = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Planet.class);

		assertThat(foundedPlanet.getTerrain()).isEqualTo(planet.getTerrain());
		assertThat(foundedPlanet.getName()).isEqualTo(planet.getName());
		assertThat(foundedPlanet.getClimate()).isEqualTo(planet.getClimate());
		assertThat(foundedPlanet.getId()).isNotEmpty();
		assertThat(foundedPlanet.getCreatedDate()).isNotNull();
		assertThat(foundedPlanet.getLastModifiedDate()).isNotNull();
	}

	@Test
	void Should_FindByNameReturnNotFound_When_NotExists() throws Exception {
		this.mockMvc.perform(
			get("/api/v1/planets/name/{name}", "nonexistent") // <-- nonexistent name
		)
			.andExpect(status().isNotFound());
	}


	@Test
	void Should_FindByIdReturnNotFound_When_NotExists() throws Exception {
		this.mockMvc.perform(
			get("/api/v1/planets/{id}", "nonexistent") // <-- nonexistent id
		)
			.andExpect(status().isNotFound());
	}

	@Test
	void Should_FindByIdReturnPlanet_When_Exists() throws Exception {

		Planet planet = Planet.builder()
			.terrain(TERRAIN)
			.name(UUID.randomUUID().toString())
			.climate(CLIMATE)
			.build();

		Planet createdPlanet = planetRepository.save(planet);


		MvcResult mvcResult = this.mockMvc.perform(
			get("/api/v1/planets/{id}", createdPlanet.getId())
		)
			.andExpect(status().isOk()).andReturn();

		Planet foundedPlanet = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Planet.class);

		assertThat(foundedPlanet.getTerrain()).isEqualTo(planet.getTerrain());
		assertThat(foundedPlanet.getName()).isEqualTo(planet.getName());
		assertThat(foundedPlanet.getClimate()).isEqualTo(planet.getClimate());
		assertThat(foundedPlanet.getId()).isEqualTo(createdPlanet.getId());
		assertThat(foundedPlanet.getCreatedDate()).isEqualTo(createdPlanet.getCreatedDate());
		assertThat(foundedPlanet.getLastModifiedDate()).isEqualTo(createdPlanet.getLastModifiedDate());
	}


	@Test
	void Should_DeleteByIdReturnNotFound_When_NotExists() throws Exception {
		this.mockMvc.perform(
			delete("/api/v1/planets/{id}", "nonexistent") // <-- nonexistent id
		)
			.andExpect(status().isNotFound());
	}

	@Test
	void Should_DeleteByIdReturnSuccess_When_Exists() throws Exception {

		Planet planet = Planet.builder()
			.terrain(TERRAIN)
			.name(UUID.randomUUID().toString())
			.climate(CLIMATE)
			.build();

		Planet createdPlanet = planetRepository.save(planet);

		MvcResult mvcResult = this.mockMvc.perform(
			delete("/api/v1/planets/{id}", createdPlanet.getId())
		)
			.andExpect(status().isNoContent()).andReturn();

		Optional<Planet> deletedPlanet = planetRepository.findById(createdPlanet.getId());

		assertThat(deletedPlanet).isNotPresent();
	}

}
