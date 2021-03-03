package com.alantech.starwarsplanets.config;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class LoggingAspectConfigurationSmokeTest {

	@Test
	void Should_Initialize_When_Starts() {
		LoggingAspectConfiguration loggingAspectConfiguration = new LoggingAspectConfiguration();
		assertThat(loggingAspectConfiguration).isNotNull();
		loggingAspectConfiguration.loggingAspect(null);
	}
}
