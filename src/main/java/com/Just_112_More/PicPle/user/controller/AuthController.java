package com.Just_112_More.PicPle.user.controller;

import com.Just_112_More.PicPle.common.ApiResponse;
import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.Just_112_More.PicPle.user.dto.LoginRequest;
import com.Just_112_More.PicPle.user.dto.LoginResponse;
import com.Just_112_More.PicPle.user.dto.ReissueRequest;
import com.Just_112_More.PicPle.user.repository.UserRepository;
import com.Just_112_More.PicPle.user.service.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.Just_112_More.PicPle.security.jwt.JwtUtil;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest loginDto) {
        LoginResponse loginResponse = authService.loginViaOAuth(loginDto);
        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }

    @PostMapping("/reissue/token")
    public ResponseEntity<ApiResponse<?>> reissue(@RequestBody ReissueRequest request) {
        String refreshToken = request.getRefreshToken();

        // 1. refreshToken 유효성 검증
        jwtUtil.validateRefreshToken(refreshToken); // JWTExcetpion발생시 우회됨

        // 2. userId 추출
        Long userId = jwtUtil.extractUserId(refreshToken, true);

        // 3. Redis에 저장된 refreshToken과 일치하는지 확인
        String savedToken = redisTemplate.opsForValue().get("RT:" + userId);
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            //return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            throw new CustomException(ErrorCode.MISMATCHED_REFRESH_TOKEN);
        }

        // 4. 사용자 조회
        com.Just_112_More.PicPle.user.domain.User user = userRepository.findOne(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 5. 새로운 accessToken 발급
        List<String> authorities = List.of(user.getRole().name());
        String newAccessToken = jwtUtil.createAccessToken(userId, authorities);

        // +) 추가 : RefreshToken 만료 임박 시 재발급
        Date expiration = jwtUtil.extractExpiration(refreshToken, true);
        long daysLeft = Duration.between(Instant.now(), expiration.toInstant()).toDays();
        String newRefreshToken = refreshToken;

        if (daysLeft < 3) {  // 임박 기준 : 3일 미만
            newRefreshToken = jwtUtil.createRefreshToken(userId);
            try {
                redisTemplate.opsForValue().set("RT:" + userId, newRefreshToken, Duration.ofDays(14));
            } catch (Exception e) {
                throw new CustomException(ErrorCode.REDIS_SAVE_FAIL);
            }
        }
        return ResponseEntity.ok(ApiResponse.success(new LoginResponse(newAccessToken, refreshToken)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        if (token == null) {
            throw new CustomException(ErrorCode.ACCESS_TOKEN_MISSING);
        }
        jwtUtil.validateAccessToken(token); // JWTExcetpion발생시 우회됨
        Long userId = jwtUtil.extractUserId(token, false);

        authService.logout(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<ApiResponse<?>> withdraw(HttpServletRequest request){
        String token = jwtUtil.resolveToken(request);
        if(token==null){
            throw new CustomException(ErrorCode.ACCESS_TOKEN_MISSING);
        }
        jwtUtil.validateAccessToken(token);
        Long userId = jwtUtil.extractUserId(token, false);

        authService.withdraw(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
