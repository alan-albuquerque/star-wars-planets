package com.alantech.starwarsplanets.service;

import java.util.Optional;

import com.alantech.starwarsplanets.domain.Planet;
import com.alantech.starwarsplanets.dto.PlanetDTO;
import com.alantech.starwarsplanets.service.exception.PlanetAlreadyExistsException;
import com.alantech.starwarsplanets.repository.PlanetRepository;
import com.alantech.starwarsplanets.service.mapper.PlanetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanetService {

	private final PlanetRepository planetRepository;
	private final PlanetMapper planetMapper;

	public Planet create(PlanetDTO planetDTO) {
		planetRepository
			.findByName(planetDTO.getName())
			.ifPresent(planet -> {
				throw new PlanetAlreadyExistsException();
			});
		Planet planet = planetMapper.planetDTOToPlanet(planetDTO);
		return planetRepository.save(planet);
	}

	public Page<Planet> findAll(Pageable pageable) {
		return planetRepository.findAll(pageable);
	}

	public Optional<Planet> findByName(String name) {
		return planetRepository.findByName(name);
	}
}
