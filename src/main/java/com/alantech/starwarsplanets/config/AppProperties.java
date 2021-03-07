package com.alantech.starwarsplanets.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Star Wars Planets.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
@Data
public class AppProperties {
	public static final String DEVELOPMENT_PROFILE = "dev";

	public final StarWarsApi starWarsApi = new StarWarsApi();

	@Data
	public static class StarWarsApi {
		private String url;
		private Integer timeout;
	}
}
