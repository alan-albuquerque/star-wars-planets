package com.alantech.starwarsplanets.controller.v1;

import com.alantech.starwarsplanets.domain.Planet;
import com.alantech.starwarsplanets.dto.PlanetDTO;
import com.alantech.starwarsplanets.service.PlanetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/planets")
@RequiredArgsConstructor
public class PlanetController {

	private final PlanetService planetService;

	@PostMapping("")
	public ResponseEntity<Planet> create(@Valid @RequestBody PlanetDTO planetDTO) {
		Planet planet = planetService.create(planetDTO);
		return new ResponseEntity<>(planet, HttpStatus.CREATED);
	}
}
