package com.alantech.starwarsplanets.service.impl;

import java.util.Objects;
import java.util.Optional;

import com.alantech.starwarsplanets.config.AppProperties;
import com.alantech.starwarsplanets.config.CacheConfig;
import com.alantech.starwarsplanets.domain.Planet;
import com.alantech.starwarsplanets.dto.CreatePlanetDTO;
import com.alantech.starwarsplanets.exception.PlanetAlreadyExistsException;
import com.alantech.starwarsplanets.exception.SwapResourceNotFoundException;
import com.alantech.starwarsplanets.network.swapi.model.SwapiPlanet;
import com.alantech.starwarsplanets.repository.PlanetRepository;
import com.alantech.starwarsplanets.service.PlanetService;
import com.alantech.starwarsplanets.service.SwapiService;
import com.alantech.starwarsplanets.service.mapper.PlanetMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanetServiceImpl implements PlanetService {

	private final PlanetRepository planetRepository;
	private final SwapiService swapiService;
	private final PlanetMapper planetMapper;
	private final AppProperties appProperties;
	private final CacheManager cacheManager;

	public Planet create(CreatePlanetDTO createPlanetDTO) {
		planetRepository
			.findByName(createPlanetDTO.getName())
			.ifPresent(planet -> {
				throw new PlanetAlreadyExistsException();
			});
		Planet planet = planetMapper.createPlanetDTOToPlanet(createPlanetDTO);
		if (planet.getName() != null) {
			try {
				this.enrichPlanetData(planet);
			}
			catch (SwapResourceNotFoundException e) {
				log.warn("Planet \"{}\" was not enriched before creation because it not exists on swapi.", planet.getName());
			}
			catch (Exception e) {
				log.error("Error on enrich planet {} with swapi data before creation.", planet, e);
			}
		}
		Planet createdPlanet = planetRepository.save(planet);
		this.clearCaches(createdPlanet);
		return createdPlanet;
	}

	public Page<Planet> findAll(Pageable pageable) {
		return planetRepository.findAll(pageable);
	}

	@Cacheable(CacheConfig.PLANET_BY_NAME)
	public Optional<Planet> findByName(String name) {
		return planetRepository.findByName(name);
	}

	@Cacheable(CacheConfig.PLANET_BY_ID)
	public Optional<Planet> findById(String id) {
		return planetRepository.findById(id);
	}

	public void deleteById(String id) {
		findById(id)
			.ifPresent(
				planet -> {
					planetRepository.deleteById(planet.getId());
					this.clearCaches(planet);
				}
			);
	}

	private void clearCaches(Planet planet) {
		if (planet.getId() != null) {
			Objects.requireNonNull(cacheManager.getCache(CacheConfig.PLANET_BY_ID)).evict(planet.getId());
		}
		if (planet.getName() != null) {
			Objects.requireNonNull(cacheManager.getCache(CacheConfig.PLANET_BY_NAME)).evict(planet.getName());
		}
	}

	public void enrichPlanetData(Planet planet) {
		SwapiPlanet swapiPlanet = swapiService.getPlanetByName(planet.getName());
		planet.setFilmsCount(swapiPlanet.getFilmsCount());
	}


	@Scheduled(cron = "${application.planets-enrichment.cron}")
	@SchedulerLock(name = "scheduledPlanetDataEnrichment")
	public void startPlanetDataEnrichment() {
		if (Boolean.FALSE.equals(appProperties.getPlanetsEnrichment().getEnabled())) { // avoiding exception if null
			return;
		}
		log.info("Start updating planets enrichment data ...");
		this.planetRepository.findAll()
			.forEach(planet -> {
				log.info("Trying to enrich data of planet: {}", planet);
				try {
					this.enrichPlanetData(planet);
					this.planetRepository.save(planet);
					this.clearCaches(planet);
					log.info("Success to enrich data of planet: {}", planet);
				}
				catch (SwapResourceNotFoundException e) {
					log.info("Enrichment failed because planet does not exists on the resource API: {}", planet);
				}
			});
		log.info("End of updating planets enrichment data.");
	}
}
