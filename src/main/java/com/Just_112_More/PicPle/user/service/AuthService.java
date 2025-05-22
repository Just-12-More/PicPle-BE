package com.Just_112_More.PicPle.user.service;

import com.Just_112_More.PicPle.security.jwt.JwtUtil;
import com.Just_112_More.PicPle.security.oauth.OAuth2TokenVerifier;
import com.Just_112_More.PicPle.security.oauth.OAuth2VerifierFactory;
import com.Just_112_More.PicPle.security.oauth.OAuthUserInfo;
import com.Just_112_More.PicPle.security.principal.UserPrincipal;
import com.Just_112_More.PicPle.user.domain.User;
import com.Just_112_More.PicPle.user.dto.LoginRequest;
import com.Just_112_More.PicPle.user.dto.LoginResponse;
import com.Just_112_More.PicPle.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OAuth2VerifierFactory verifierFactory;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public LoginResponse loginViaOAuth(LoginRequest request) {
        OAuth2TokenVerifier verifier = verifierFactory.getVerifier(request.getProvider());
        OAuthUserInfo userInfo = verifier.verify(request.getAccessToken());

        User user = userRepository.findByProviderAndProviderId(userInfo.getProvider(), userInfo.getProviderId())
                .orElseGet(() -> userRepository.save(User.fromOAuth(userInfo)));

        List<String> authorities = List.of(user.getRole().name());
        String accessToken = jwtUtil.createAccessToken(user.getId(), authorities);
        String refreshToken = jwtUtil.createRefreshToken(user.getId());

        // Redis에 refreshToken 저장
        redisTemplate.opsForValue().set("RT:" + user.getId(), refreshToken, Duration.ofDays(14));

        return new LoginResponse(accessToken, refreshToken);
    }
}
