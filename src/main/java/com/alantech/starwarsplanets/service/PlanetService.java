package com.alantech.starwarsplanets.service;

import java.util.Optional;

import com.alantech.starwarsplanets.domain.Planet;
import com.alantech.starwarsplanets.dto.CreatePlanetDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlanetService {
	Planet create(CreatePlanetDTO planetDTO);

	Page<Planet> findAll(Pageable pageable);

	Optional<Planet> findByName(String name);

	Optional<Planet> findById(String name);

	void deleteById(String name);
}
