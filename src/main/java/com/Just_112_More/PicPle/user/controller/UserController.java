package com.Just_112_More.PicPle.user.controller;

import com.Just_112_More.PicPle.common.ApiResponse;
import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.Just_112_More.PicPle.photo.service.S3Service;
import com.Just_112_More.PicPle.security.jwt.JwtUtil;
import com.Just_112_More.PicPle.user.domain.User;
import com.Just_112_More.PicPle.user.dto.ProfileDto;
import com.Just_112_More.PicPle.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
    private final S3Service s3Service;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<?>> getUserInfo(HttpServletRequest request){
        String token = jwtUtil.resolveToken(request);
        if (token == null) {
            throw new CustomException(ErrorCode.ACCESS_TOKEN_MISSING);
        }
        jwtUtil.validateAccessToken(token);
        Long userId = jwtUtil.extractUserId(token, false);

        ProfileDto userInfo = userService.getUsernameAndProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(userInfo));
    }

}
