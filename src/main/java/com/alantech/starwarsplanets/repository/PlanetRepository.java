package com.alantech.starwarsplanets.repository;

import com.alantech.starwarsplanets.domain.Planet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PlanetRepository extends MongoRepository<Planet, String> {
	Page<Planet> findAll(Pageable pageable);
	Optional<Planet> findByName(String name);
}
