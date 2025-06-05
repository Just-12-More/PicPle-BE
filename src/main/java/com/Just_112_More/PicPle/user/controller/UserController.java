package com.Just_112_More.PicPle.user.controller;

import com.Just_112_More.PicPle.common.ApiResponse;
import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.Just_112_More.PicPle.photo.service.S3Service;
import com.Just_112_More.PicPle.security.jwt.JwtUtil;
import com.Just_112_More.PicPle.user.dto.ProfileDto;
import com.Just_112_More.PicPle.user.dto.ProfileWithImageDto;
import com.Just_112_More.PicPle.user.service.UserService;
import com.amazonaws.util.IOUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
@Slf4j
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

        ProfileDto profile = userService.getUsernameAndProfile(userId);

        String key = profile.getProfileURL();
        try(InputStream is = s3Service.getObjectStream(key)) {
            Resource resource = new InputStreamResource(is);
            //byte[] imageBytes = IOUtils.toByteArray(is);
            ProfileWithImageDto dto = new ProfileWithImageDto(profile.getUsername(), resource);
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)  // 콘텐츠 타입 설정
                    .contentLength(resource.contentLength())
                    .body(ApiResponse.success(dto));
        } catch (IOException e){
            log.error("프로필 이미지 로드 실패, 사용자 ID: {}, 이미지 키: {}", userId, key, e);
            throw new CustomException(ErrorCode.USER_IMAGE_FAIL);
        }
    }

}
