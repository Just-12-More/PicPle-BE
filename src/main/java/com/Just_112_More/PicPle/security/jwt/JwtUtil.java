package com.Just_112_More.PicPle.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.Just_112_More.PicPle.security.jwt.JwtFilter.AUTHORIZATION_HEADER;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private final JwtProperties jwtProperties;

    // 서명 키
    private Key accessKey;
    private Key refreshKey;

    @PostConstruct
    public void init() throws Exception {
        if (jwtProperties.getAccessSecret() == null || jwtProperties.getRefreshSecret() == null) {
            throw new IllegalStateException("JWT secrets must not be null");
        }

        this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getAccessSecret()));
        this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getRefreshSecret()));
        System.out.println("[DEBUG] accessSecret = " + jwtProperties.getAccessSecret());
        System.out.println("[DEBUG] refreshSecret = " + jwtProperties.getRefreshSecret());
    }

    // AccessToken - 일반 요청 인증 용
    public String createAccessToken(Long userId, List<String> authorities) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("authorities", String.join(",", authorities))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getAccessTokenValidityInSeconds()))
                .signWith(accessKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // RefreshToken - 재발급용
    public String createRefreshToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getRefreshTokenValidityInSeconds()))
                .signWith(refreshKey, SignatureAlgorithm.HS512)
                .compact();
    }

    // accessToken 인증정보 추출
    public Authentication getAuthenticationFromAccessToken(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("authorities").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // 유효성 검사 (용도별 분리)
    public boolean validateAccessToken(String token) {
        return validate(token, accessKey);
    }

    public boolean validateRefreshToken(String token) {
        return validate(token, refreshKey);
    }

    public boolean validate(String token, Key key) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // 공통 메서드 - userId 추출
    public Long extractUserId(String token, boolean isRefresh) {
        Key key = isRefresh ? refreshKey : accessKey;
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    // refreshToken 만료 시간 확인
    public Date extractExpiration(String token, boolean isRefresh) {
        Key key = isRefresh ? refreshKey : accessKey;
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    // 공용 헤더의 accesstoken추출 메서드
     public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

}
