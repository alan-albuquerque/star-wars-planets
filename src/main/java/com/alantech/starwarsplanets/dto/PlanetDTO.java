package com.alantech.starwarsplanets.dto;

import javax.validation.constraints.NotEmpty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanetDTO {
	private String id;
	@NotEmpty
	private String name;
	@NotEmpty
	private String climate;
	@NotEmpty
	private String terrain;
}
