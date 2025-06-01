package com.Just_112_More.PicPle.photo.controller;

import com.Just_112_More.PicPle.common.ApiResponse;
import com.Just_112_More.PicPle.photo.dto.GetS3UrlDto;
import com.Just_112_More.PicPle.photo.service.S3Service;
import com.Just_112_More.PicPle.user.domain.AuthUser;
import com.Just_112_More.PicPle.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/s3")
public class S3Controller {
    private final S3Service s3Service;

    @GetMapping("/posturl")
    public ResponseEntity<ApiResponse<GetS3UrlDto>> getPostS3Url(
            @AuthUser User user,
            @RequestParam String filename) {
        try {
            GetS3UrlDto dto = s3Service.getPostS3Url(user.getId(), filename);
            return ResponseEntity.ok(ApiResponse.success(dto)); // 성공 응답
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(null, "INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }

    @GetMapping("/geturl")
    public ResponseEntity<ApiResponse<GetS3UrlDto>> getGetS3Url(
            @AuthUser User user,
            @RequestParam String filename) {
        try {
            GetS3UrlDto dto = s3Service.getGetS3Url(user.getId(), filename);
            return ResponseEntity.ok(ApiResponse.success(dto)); // 성공 응답
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(null, "INTERNAL_ERROR", "서버 오류가 발생했습니다."));
        }
    }

}
