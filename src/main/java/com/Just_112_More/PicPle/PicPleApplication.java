package com.Just_112_More.PicPle;

import com.Just_112_More.PicPle.security.jwt.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class PicPleApplication {

	public static void main(String[] args) {
		SpringApplication.run(PicPleApplication.class, args);
	}

}
