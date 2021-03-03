package com.alantech.starwarsplanets;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StarWarsPlanetsApplicationTests {

	@Test
	@SuppressWarnings("java:S2699")
	void smokeTestApplication() {
		StarWarsPlanetsApplication.main(new String[]{});
	}

}
