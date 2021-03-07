package com.alantech.starwarsplanets.network.swapi.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SwapiPlanet implements Serializable {
	private String name;
	private String[] films;

	public Integer getFilmsCount() {
		return this.films == null ? 0 : this.films.length;
	}
}
