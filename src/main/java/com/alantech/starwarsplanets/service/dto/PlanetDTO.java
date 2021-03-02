package com.alantech.starwarsplanets.service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanetDTO {
	private String id;
	private String name;
	private String climate;
	private String terrain;
}
