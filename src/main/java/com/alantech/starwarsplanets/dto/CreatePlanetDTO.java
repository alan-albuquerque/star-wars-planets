package com.alantech.starwarsplanets.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreatePlanetDTO {
	private final String id;
	@NotEmpty
	private final String name;
	@NotEmpty
	private final String climate;
	@NotEmpty
	private final String terrain;
}
