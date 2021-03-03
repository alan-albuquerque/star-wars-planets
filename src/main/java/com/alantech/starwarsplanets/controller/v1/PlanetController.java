package com.alantech.starwarsplanets.controller.v1;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import com.alantech.starwarsplanets.domain.Planet;
import com.alantech.starwarsplanets.dto.PlanetDTO;
import com.alantech.starwarsplanets.exception.ResourceNotFoundException;
import com.alantech.starwarsplanets.service.PlanetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/planets")
@RequiredArgsConstructor
@Validated
public class PlanetController {

	private final PlanetService planetService;

	@PostMapping("")
	public ResponseEntity<Planet> create(@Valid @RequestBody PlanetDTO planetDTO) {
		Planet planet = planetService.create(planetDTO);
		return new ResponseEntity<>(planet, HttpStatus.CREATED);
	}

	@GetMapping("/search")
	public ResponseEntity<List<Planet>> search(@RequestParam @NotBlank String query) {
		List<Planet> planets = planetService.searchByName(query);
		if (planets.isEmpty()) {
			throw new ResourceNotFoundException();
		}
		return new ResponseEntity<>(planets, HttpStatus.OK);
	}
}
