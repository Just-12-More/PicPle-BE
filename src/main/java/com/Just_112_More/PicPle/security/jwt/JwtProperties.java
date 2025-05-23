package com.Just_112_More.PicPle.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String header;
    private String accessSecret;
    private String refreshSecret;
    private long accessTokenValidityInSeconds;
    private long refreshTokenValidityInSeconds;
}
