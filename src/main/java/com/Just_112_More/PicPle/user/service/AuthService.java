package com.Just_112_More.PicPle.user.service;

import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
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
import java.util.UUID;

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
                // 탈퇴회원 복구
                .map( existing -> {
                    if(existing.isDeleted()) {
                        existing.reactivate();
                        userRepository.save(existing);
                    }
                    return existing;
                })
                // 신규회원가입처리(기본 사용자이름 지정)
                .orElseGet(() -> {
                    User newUser = User.fromOAuth(userInfo);
                    userRepository.save(newUser);

                    String uuid = UUID.randomUUID().toString().substring(0, 5);
                    String defaultNickname = "picple-user-" + newUser.getId() + "-" + uuid;

                    newUser.setUserName(defaultNickname);
                    return userRepository.save(newUser);
                });

        List<String> authorities = List.of(user.getRole().name());
        String accessToken = jwtUtil.createAccessToken(user.getId(), authorities);
        String refreshToken = jwtUtil.createRefreshToken(user.getId());

        try {
            // Redis에 refreshToken 저장
            redisTemplate.opsForValue().set("RT:" + user.getId(), refreshToken, Duration.ofDays(14));
        } catch (Exception e) {
            throw new CustomException(ErrorCode.REDIS_SAVE_FAIL);
        }

        return new LoginResponse(accessToken, refreshToken);
    }

    public void logout(Long userId){
        redisTemplate.delete("RT:"+userId);
    }

    public void withdraw(Long userId) {
        User user = userRepository.findOne(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        try {
            user.deleteUser();
        } catch (IllegalStateException e){
            throw new CustomException(ErrorCode.USER_ALREADY_DELETED);
        }
        userRepository.save(user);
    }
}
