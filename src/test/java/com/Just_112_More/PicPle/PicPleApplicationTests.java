package com.Just_112_More.PicPle;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
		"jwt.access-secret=test-access",
		"jwt.refresh-secret=test-refresh"
})
class PicPleApplicationTests {

	@Test
	void contextLoads() {
	}

}
