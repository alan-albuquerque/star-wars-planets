package com.alantech.starwarsplanets.service.mapper;

import com.alantech.starwarsplanets.domain.Planet;
import com.alantech.starwarsplanets.dto.CreatePlanetDTO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@Lazy
public class PlanetMapper {
	public Planet createPlanetDTOToPlanet(CreatePlanetDTO planetDTO) {
		return Planet.builder()
			.name(planetDTO.getName())
			.climate(planetDTO.getClimate())
			.terrain(planetDTO.getTerrain())
			.build();
	}
}
