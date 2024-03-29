package com.alantech.starwarsplanets.controller.v1;

import com.alantech.starwarsplanets.domain.Planet;
import com.alantech.starwarsplanets.dto.CreatePlanetDTO;
import com.alantech.starwarsplanets.exception.ResourceAlreadyExistsException;
import com.alantech.starwarsplanets.exception.ResourceNotFoundException;
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
	public ResponseEntity<Planet> create(@Valid @RequestBody CreatePlanetDTO createPlanetDTO) {
		Optional<Planet> planet = planetService.findByName(createPlanetDTO.getName());
		if (planet.isPresent()) {
			throw new ResourceAlreadyExistsException("A planet with the same name already exists.");
		}
		Planet createdPlanet = planetService.create(createPlanetDTO);
		return new ResponseEntity<>(createdPlanet, HttpStatus.CREATED);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<Object> delete(@PathVariable("id") String id) {
		Optional<Planet> planet = planetService.findById(id);
		if (planet.isPresent()) {
			planetService.deleteById(id);
			return ResponseEntity.noContent().build();
		}
		throw new ResourceNotFoundException();
	}

	@GetMapping("")
	public ResponseEntity<Page<Planet>> list(Pageable pageable) {
		Page<Planet> planets = planetService.findAll(pageable);
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

	@GetMapping("{id}")
	public ResponseEntity<Planet> findById(@PathVariable("id") String id) {
		Optional<Planet> planet = planetService.findById(id);
		if (planet.isPresent()) {
			return new ResponseEntity<>(planet.get(), HttpStatus.OK);
		}
		throw new ResourceNotFoundException();
	}

}
