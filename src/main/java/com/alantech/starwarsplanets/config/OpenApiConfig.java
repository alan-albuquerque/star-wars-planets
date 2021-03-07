package com.alantech.starwarsplanets.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI(@Value("${application.name}") String appName, @Value("${application.version}") String appVersion) {
		return new OpenAPI()
			.info(new Info()
				.title(appName)
				.version(appVersion)
				.description(appName));
	}
}
