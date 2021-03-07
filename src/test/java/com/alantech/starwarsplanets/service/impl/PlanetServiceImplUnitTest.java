package com.alantech.starwarsplanets.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.alantech.starwarsplanets.IntegrationTest;
import com.alantech.starwarsplanets.config.AppProperties;
import com.alantech.starwarsplanets.config.CacheConfig;
import com.alantech.starwarsplanets.domain.Planet;
import com.alantech.starwarsplanets.dto.CreatePlanetDTO;
import com.alantech.starwarsplanets.exception.SwapResourceNotFoundException;
import com.alantech.starwarsplanets.network.swapi.model.SwapiPlanet;
import com.alantech.starwarsplanets.repository.PlanetRepository;
import com.alantech.starwarsplanets.service.SwapiService;
import com.alantech.starwarsplanets.service.mapper.PlanetMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@IntegrationTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class PlanetServiceImplUnitTest {
	private final PlanetServiceImpl planetService;

	@MockBean
	private PlanetRepository planetRepository;
	@MockBean
	private SwapiService swapiService;
	@MockBean
	private PlanetMapper planetMapper;
	@MockBean
	private AppProperties appProperties;
	@MockBean
	private CacheManager cacheManager;
	@Mock
	private Cache cache;

	AppProperties.PlanetsEnrichment planetsEnrichment = new AppProperties.PlanetsEnrichment();

	@Test
	void Should_PlanetEnrichmentRun_When_Enabled() {
		planetsEnrichment.setCron("");
		planetsEnrichment.setEnabled(true);
		when(appProperties.getPlanetsEnrichment()).thenReturn(planetsEnrichment);
		planetService.startPlanetDataEnrichment();
		verify(this.planetRepository, times(1)).findAll();
	}

	@Test
	void Should_PlanetEnrichmentNotRun_When_Disabled() {
		planetsEnrichment.setCron("");
		planetsEnrichment.setEnabled(false);
		when(this.appProperties.getPlanetsEnrichment()).thenReturn(planetsEnrichment);
		planetService.startPlanetDataEnrichment();
		verify(this.planetRepository, never()).findAll();
	}

	@Test
	void Should_PlanetEnrichmentUpdatePlanets_When_FoundItems() {
		planetsEnrichment.setCron("");
		planetsEnrichment.setEnabled(true);
		Planet planet = Planet.builder()
			.id(UUID.randomUUID().toString())
			.name(UUID.randomUUID().toString())
			.filmsCount(0)
			.build();
		SwapiPlanet swapiPlanet = SwapiPlanet.builder()
			.name(planet.getName())
			.films(new String[]{"one", "two"})
			.build();
		when(planetRepository.findAll()).thenReturn(List.of(planet));
		when(swapiService.getPlanetByName(planet.getName())).thenReturn(swapiPlanet);
		when(appProperties.getPlanetsEnrichment()).thenReturn(planetsEnrichment);
		when(cacheManager.getCache(CacheConfig.PLANET_BY_ID)).thenReturn(cache);
		when(cacheManager.getCache(CacheConfig.PLANET_BY_NAME)).thenReturn(cache);

		planetService.startPlanetDataEnrichment();

		verify(swapiService, times(1)).getPlanetByName(planet.getName());
		verify(planetRepository, times(1)).save(planet);
		verify(cacheManager.getCache(CacheConfig.PLANET_BY_ID), times(1)).evict(planet.getId());
		verify(cacheManager.getCache(CacheConfig.PLANET_BY_NAME), times(1)).evict(planet.getName());
	}

	@Test
	void Should_PlanetEnrichmentUpdatePlanetsSilently_When_NotFoundSwapiPlanet() {
		planetsEnrichment.setCron("");
		planetsEnrichment.setEnabled(true);
		Planet planet = Planet.builder()
			.id(UUID.randomUUID().toString())
			.name(UUID.randomUUID().toString())
			.filmsCount(0)
			.build();
		when(planetRepository.findAll()).thenReturn(List.of(planet));
		when(swapiService.getPlanetByName(planet.getName())).thenThrow(new SwapResourceNotFoundException("Planet not found"));
		when(appProperties.getPlanetsEnrichment()).thenReturn(planetsEnrichment);
		when(cacheManager.getCache(CacheConfig.PLANET_BY_ID)).thenReturn(cache);
		when(cacheManager.getCache(CacheConfig.PLANET_BY_NAME)).thenReturn(cache);

		planetService.startPlanetDataEnrichment();

		verify(planetRepository, never()).save(planet);
		verify(cacheManager.getCache(CacheConfig.PLANET_BY_ID), never()).evict(planet.getId());
		verify(cacheManager.getCache(CacheConfig.PLANET_BY_NAME), never()).evict(planet.getName());
	}

	@Test
	void Should_CreateFailSilently_When_NotFoundSwapiPlanet() {
		Planet planet = Planet.builder()
			.id(UUID.randomUUID().toString())
			.name(UUID.randomUUID().toString())
			.filmsCount(0)
			.build();
		CreatePlanetDTO createPlanetDTO = CreatePlanetDTO.builder()
			.id(planet.getId())
			.name(planet.getName())
			.build();

		when(cacheManager.getCache(CacheConfig.PLANET_BY_ID)).thenReturn(cache);
		when(cacheManager.getCache(CacheConfig.PLANET_BY_NAME)).thenReturn(cache);
		when(planetMapper.createPlanetDTOToPlanet(createPlanetDTO)).thenReturn(planet);
		when(planetRepository.save(planet)).thenReturn(planet);
		when(swapiService.getPlanetByName(planet.getName())).thenThrow(new SwapResourceNotFoundException("any error"));

		planetService.create(createPlanetDTO);

		verify(planetRepository, times(1)).save(planet);
		verify(cacheManager.getCache(CacheConfig.PLANET_BY_ID), times(1)).evict(planet.getId());
		verify(cacheManager.getCache(CacheConfig.PLANET_BY_NAME), times(1)).evict(planet.getName());
	}
}
