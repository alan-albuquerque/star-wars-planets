package com.alantech.starwarsplanets.repository;

import java.util.List;

import com.alantech.starwarsplanets.domain.Planet;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlanetRepository extends MongoRepository<Planet, String> {
	List<Planet> findAllByNameStartsWith(String name);
}
