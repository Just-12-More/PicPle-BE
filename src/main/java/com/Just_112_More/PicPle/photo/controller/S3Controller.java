package com.Just_112_More.PicPle.photo.controller;

import com.Just_112_More.PicPle.common.ApiResponse;
import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.Just_112_More.PicPle.photo.dto.GetS3UrlDto;
import com.Just_112_More.PicPle.photo.dto.GetS3UrlRequest;
import com.Just_112_More.PicPle.photo.service.S3Service;
import com.Just_112_More.PicPle.security.jwt.JwtUtil;
//import com.Just_112_More.PicPle.user.domain.AuthUser;
//import com.Just_112_More.PicPle.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/s3")
public class S3Controller {
    private final S3Service s3Service;
    private final JwtUtil jwtUtil;

    @PostMapping("/posturl")
    public ResponseEntity<ApiResponse<?>> getPostS3Url(
            HttpServletRequest request,
            @RequestBody GetS3UrlRequest s3Request) {
        try {
            String token = jwtUtil.resolveToken(request);
            if (token == null) {
                throw new CustomException(ErrorCode.ACCESS_TOKEN_MISSING);
            }
            jwtUtil.validateAccessToken(token);
            Long userId = jwtUtil.extractUserId(token, false);

            String filename = s3Request.getFilename();
            GetS3UrlDto dto = s3Service.getPostS3Url(userId, filename);
            return ResponseEntity.ok(ApiResponse.success(dto));
        } catch (CustomException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail(null, e.getErrorCode().name(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(null, "INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }


    @PostMapping("/geturl")
    public ResponseEntity<ApiResponse<?>> getGetS3Url(
            HttpServletRequest request,
            @RequestBody GetS3UrlRequest s3Request) {
        try {
            String token = jwtUtil.resolveToken(request);
            if (token == null) {
                throw new CustomException(ErrorCode.ACCESS_TOKEN_MISSING);
            }

            jwtUtil.validateAccessToken(token);
            Long userId = jwtUtil.extractUserId(token, false);

            String filename = s3Request.getFilename();
            GetS3UrlDto dto = s3Service.getGetS3Url(userId, filename);

            return ResponseEntity.ok(ApiResponse.success(dto));
        } catch (CustomException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail(null, e.getErrorCode().name(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(null, "INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }


}
