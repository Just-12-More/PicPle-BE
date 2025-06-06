package com.Just_112_More.PicPle.like.controller;

import com.Just_112_More.PicPle.common.ApiResponse;
import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.photo.repository.PhotoRepository;
import com.Just_112_More.PicPle.photo.service.PhotoService;
import com.Just_112_More.PicPle.security.jwt.JwtUtil;
import com.Just_112_More.PicPle.user.domain.User;
import com.Just_112_More.PicPle.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/photos")
public class LikeController {
    private final PhotoRepository photoRepository;
    private final PhotoService photoService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/{photo_id}/like")
    public ResponseEntity<ApiResponse<?>> likePhoto(
            @PathVariable("photo_id") Long photoId,
            HttpServletRequest request) {

        String token = jwtUtil.resolveToken(request);

        Long userId = jwtUtil.extractUserId(token, false);

        User user = userRepository.findOne(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Photo photo = photoRepository.getPhotoById(photoId);
        photoService.addLike(photo, user);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
