package com.alantech.starwarsplanets.network.swapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class SwapiPlanet {
	private String name;
	@JsonProperty("rotation_period")
	private String rotationPeriod;
	@JsonProperty("orbital_period")
	private String orbitalPeriod;
	private String diameter;
	private String climate;
	private String gravity;
	private String terrain;
	@JsonProperty("surface_water")
	private String surfaceWater;
	private String population;
	private String[] residents;
	private String[] films;
	private Date created;
	private Date edited;
	private String url;

	public Integer getFilmsCount() {
		return this.films == null ? 0 : this.films.length;
	}
}
