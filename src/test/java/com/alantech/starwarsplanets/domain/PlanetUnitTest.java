package com.alantech.starwarsplanets.domain;

import com.alantech.starwarsplanets.common.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlanetUnitTest {
	@Test
	void Should_ValidateEqualsVerifier_When_Valid() throws Exception {
		TestUtil.equalsVerifier(Planet.class);
		Planet planet = Planet.builder().id("1").build();
		Planet planet1 = Planet.builder().id("1").build();
		Planet planet2 = Planet.builder().id("2").build();
		assertThat(planet).isEqualTo(planet1).isNotEqualTo(planet2);
		assertThat(planet1).isNotEqualTo(planet2);
	}
}
