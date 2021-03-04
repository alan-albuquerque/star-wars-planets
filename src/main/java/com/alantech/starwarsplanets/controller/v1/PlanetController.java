package com.alantech.starwarsplanets.controller.v1;

import com.alantech.starwarsplanets.controller.v1.exception.ResourceAlreadyExistsException;
import com.alantech.starwarsplanets.controller.v1.exception.ResourceNotFoundException;
import com.alantech.starwarsplanets.domain.Planet;
import com.alantech.starwarsplanets.dto.PlanetDTO;
import com.alantech.starwarsplanets.service.PlanetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/planets")
@RequiredArgsConstructor
@Validated
public class PlanetController {

	private final PlanetService planetService;

	@PostMapping("")
	public ResponseEntity<Planet> create(@Valid @RequestBody PlanetDTO planetDTO) {
		Optional<Planet> planet = planetService.findByName(planetDTO.getName());
		if (planet.isPresent()) {
			throw new ResourceAlreadyExistsException("A planet with the same name already exists.");
		}
		Planet createdPlanet = planetService.create(planetDTO);
		return new ResponseEntity<>(createdPlanet, HttpStatus.CREATED);
	}

	@GetMapping("")
	public ResponseEntity<Page<Planet>> list(Pageable pageable) {
		Page<Planet> planets = planetService.findAll(pageable);
		if (planets.isEmpty()) {
			throw new ResourceNotFoundException();
		}
		return new ResponseEntity<>(planets, HttpStatus.OK);
	}

	@GetMapping("name/{name}")
	public ResponseEntity<Planet> findByName(@PathVariable("name") String name) {
		Optional<Planet> planet = planetService.findByName(name);
		if (planet.isPresent()) {
			return new ResponseEntity<>(planet.get(), HttpStatus.OK);
		}
		throw new ResourceNotFoundException();
	}

}
