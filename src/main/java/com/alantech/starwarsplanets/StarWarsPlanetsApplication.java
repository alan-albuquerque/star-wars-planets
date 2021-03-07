package com.alantech.starwarsplanets;

import com.alantech.starwarsplanets.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class})
public class StarWarsPlanetsApplication {

	public static void main(String[] args) {
		SpringApplication.run(StarWarsPlanetsApplication.class, args);
	}
}
