package com.alantech.starwarsplanets.service.impl;

import java.util.Optional;

import com.alantech.starwarsplanets.domain.Planet;
import com.alantech.starwarsplanets.dto.CreatePlanetDTO;
import com.alantech.starwarsplanets.exception.PlanetAlreadyExistsException;
import com.alantech.starwarsplanets.network.swapi.model.SwapiPlanet;
import com.alantech.starwarsplanets.repository.PlanetRepository;
import com.alantech.starwarsplanets.service.PlanetService;
import com.alantech.starwarsplanets.service.SwapiService;
import com.alantech.starwarsplanets.service.mapper.PlanetMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanetServiceImpl implements PlanetService {

	private final PlanetRepository planetRepository;
	private final SwapiService swapiService;
	private final PlanetMapper planetMapper;

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
			catch (Exception e) {
				log.error("Error on enrich planet {} with swapi data before creation.", planet, e);
			}
		}
		return planetRepository.save(planet);
	}

	public Page<Planet> findAll(Pageable pageable) {
		return planetRepository.findAll(pageable);
	}

	public Optional<Planet> findByName(String name) {
		return planetRepository.findByName(name);
	}

	public Optional<Planet> findById(String name) {
		return planetRepository.findById(name);
	}

	public void deleteById(String name) {
		planetRepository.deleteById(name);
	}

	public void enrichPlanetData(Planet planet) {
		SwapiPlanet swapiPlanet = swapiService.getPlanetByName(planet.getName());
		planet.setFilmsCount(swapiPlanet.getFilmsCount());
	}

}
