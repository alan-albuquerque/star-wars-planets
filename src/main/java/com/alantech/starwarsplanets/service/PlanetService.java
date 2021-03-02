package com.alantech.starwarsplanets.service;

import com.alantech.starwarsplanets.domain.Planet;
import com.alantech.starwarsplanets.repository.PlanetRepository;
import com.alantech.starwarsplanets.service.dto.PlanetDTO;
import com.alantech.starwarsplanets.service.mapper.PlanetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanetService {

	private final PlanetRepository planetRepository;
	private final PlanetMapper planetMapper;

	public Planet create(PlanetDTO planetDTO) {
		Planet planet = planetMapper.planetDTOToPlanet(planetDTO);
		return planetRepository.save(planet);
	}
}
