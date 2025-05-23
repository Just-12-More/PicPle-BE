package com.Just_112_More.PicPle;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;


@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
		// this-is-a-test-secret-key-for-jwt-testing-purposes-123456
		"jwt.access-secret=dGhpcy1pcy1hLXRlc3Qtc2VjcmV0LWtleS1mb3Itand0LXRlc3RpbmctcHVycG9zZXMtMTIzNDU2",
		"jwt.refresh-secret=dGhpcy1pcy1hLXRlc3Qtc2VjcmV0LWtleS1mb3Itand0LXRlc3RpbmctcHVycG9zZXMtMTIzNDU2",
		"jwt.access-token-validity-in-seconds=0",
		"jwt.refresh-token-validity-in-seconds=0"
})
class PicPleApplicationTests {

	@Test
	void contextLoads() {
	}

}
