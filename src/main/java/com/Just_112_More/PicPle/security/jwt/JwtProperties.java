package com.Just_112_More.PicPle.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    public JwtProperties() {
        System.out.println("로깅용 :: JwtProperties 생성자 호출됨!!!");
    }

    private String header;
    private String accessSecret;
    private String refreshSecret;
    private long accessTokenValidityInSeconds;
    private long refreshTokenValidityInSeconds;
}
