package com.alantech.starwarsplanets.service.impl;

import java.util.Optional;

import com.alantech.starwarsplanets.domain.Planet;
import com.alantech.starwarsplanets.dto.CreatePlanetDTO;
import com.alantech.starwarsplanets.repository.PlanetRepository;
import com.alantech.starwarsplanets.service.PlanetService;
import com.alantech.starwarsplanets.service.exception.PlanetAlreadyExistsException;
import com.alantech.starwarsplanets.service.mapper.PlanetMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanetServiceImpl implements PlanetService {

	private final PlanetRepository planetRepository;
	private final PlanetMapper planetMapper;

	public Planet create(CreatePlanetDTO planetDTO) {
		planetRepository
			.findByName(planetDTO.getName())
			.ifPresent(planet -> {
				throw new PlanetAlreadyExistsException();
			});
		Planet planet = planetMapper.createPlanetDTOToPlanet(planetDTO);
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
}
