package com.Just_112_More.PicPle.photo.controller;

import com.Just_112_More.PicPle.common.ApiResponse;
import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.photo.dto.*;
import com.Just_112_More.PicPle.photo.repository.PhotoRepository;
import com.Just_112_More.PicPle.photo.service.PhotoService;
import com.Just_112_More.PicPle.security.jwt.JwtUtil;
import com.Just_112_More.PicPle.user.domain.User;
import com.Just_112_More.PicPle.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/photos")
public class PhotoController {
    private final PhotoRepository photoRepository;
    private final PhotoService photoService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/{photo_id}/like")
    public ResponseEntity<ApiResponse<?>> likePhoto(
            @PathVariable("photo_id") Long photoId,
            HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Long userId = jwtUtil.extractUserId(token, false);

        User user = userRepository.findOne(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 사진 조회
        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new CustomException(ErrorCode.PHOTO_NOT_FOUND));

        // 좋아요 추가 처리
        photoService.addLike(photo, user);

        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
