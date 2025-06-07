package com.Just_112_More.PicPle.user.controller;

import com.Just_112_More.PicPle.common.ApiResponse;
import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.photo.dto.MyPagePhotoDto;
import com.Just_112_More.PicPle.photo.dto.uploadPhotoDto;
import com.Just_112_More.PicPle.photo.service.S3Service;
import com.Just_112_More.PicPle.security.jwt.JwtUtil;
import com.Just_112_More.PicPle.user.dto.ProfileDto;
import com.Just_112_More.PicPle.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
@Slf4j
public class UserController {
    private final S3Service s3Service;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<?>> getUserInfoProfile(HttpServletRequest request){
        Long userId = extractAndValidateUserId(request);

        ProfileDto profile = userService.getUsernameAndProfile(userId);
        String key = profile.getProfilePath();
        log.info("S3 Key = {}", key);

        return ResponseEntity.ok().body(ApiResponse.success(profile));
    }

    @PostMapping("/info")
    public ResponseEntity<ApiResponse<?>> updateUserInfo( HttpServletRequest request,
                                                    @RequestParam String nickName, @RequestParam MultipartFile image ){
        Long userId = extractAndValidateUserId(request);

        String key = null;
        if(!image.isEmpty()){
            try {
                key = s3Service.uploadObject(image);
            } catch (Exception e){
                throw new CustomException(ErrorCode.USER_IMAGE_UPLOAD_FAIL);
            }
        }

        nickName = nickName.isEmpty()? null : nickName;
        ProfileDto profileDto = userService.updateProfile(userId, key, nickName);
        return ResponseEntity.ok().body(ApiResponse.success(profileDto));
    }

    @GetMapping("/photos")
    public ResponseEntity<ApiResponse<?>> getUserPhotos(HttpServletRequest request){
        Long userId = extractAndValidateUserId(request);
        List<MyPagePhotoDto> photosByUser = userService.getPhotosByUser(userId);
        return ResponseEntity.ok().body(ApiResponse.success(photosByUser));
    }

    @GetMapping("/likes")
    public ResponseEntity<ApiResponse<?>> getUserLikes(HttpServletRequest request){
        Long userId = extractAndValidateUserId(request);
        List<MyPagePhotoDto> LikePhotosByUser = userService.getLikedPhotosByUser(userId);
        return ResponseEntity.ok().body(ApiResponse.success(LikePhotosByUser));
    }

    // 요청에서 accessToken추출 -> userId추출 -> 유효성(탈퇴여부) 체크
    private Long extractAndValidateUserId(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        if (token == null) {
            throw new CustomException(ErrorCode.ACCESS_TOKEN_MISSING);
        }
        jwtUtil.validateAccessToken(token);
        Long userId = jwtUtil.extractUserId(token, false);
        return userService.validateUserId(userId);
    }


}
