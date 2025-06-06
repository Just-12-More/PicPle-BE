package com.Just_112_More.PicPle.user.controller;

import com.Just_112_More.PicPle.common.ApiResponse;
import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.photo.dto.uploadPhotoDto;
import com.Just_112_More.PicPle.photo.service.S3Service;
import com.Just_112_More.PicPle.security.jwt.JwtUtil;
import com.Just_112_More.PicPle.user.dto.NicknameDto;
import com.Just_112_More.PicPle.user.dto.ProfileDto;
import com.Just_112_More.PicPle.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
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

    @GetMapping("/info/profile")
    public ResponseEntity<Resource> getUserInfoProfile(HttpServletRequest request){
        Long userId = extractAndValidateUserId(request);

        ProfileDto profile = userService.getUsernameAndProfile(userId);
        String key = profile.getProfileURL();
        log.info("S3 Key = {}", key);

        try{
            InputStream is = s3Service.getObjectStream(key);
            Resource resource = new InputStreamResource(is);
            //byte[] imageBytes = IOUtils.toByteArray(is);
            //ProfileWithImageDto dto = new ProfileWithImageDto(profile.getUsername(), resource);

            // 확장자 추출해서 Content-Type 지정
            String contentType = MediaType.IMAGE_JPEG_VALUE; // 기본값
            if (key.endsWith(".png")) contentType = MediaType.IMAGE_PNG_VALUE;
            else if (key.endsWith(".gif")) contentType = MediaType.IMAGE_GIF_VALUE;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDisposition(ContentDisposition.inline()
                    .filename(key)  // or extract filename from key
                    .build());
            return new ResponseEntity<> (resource, headers, HttpStatus.OK);
        } catch (Exception e){
            log.error("프로필 이미지 로드 실패, 사용자 ID: {}, 이미지 키: {}", userId, key, e);
            throw new CustomException(ErrorCode.USER_IMAGE_GET_FAIL);
        }
    }

    @GetMapping("/info/nickname")
    public ResponseEntity<ApiResponse<?>> getUserInfoName(HttpServletRequest request){
        Long userId = extractAndValidateUserId(request);

        ProfileDto usernameAndProfile = userService.getUsernameAndProfile(userId);
        String nickName = usernameAndProfile.getUsername();
        if(nickName==null) throw new CustomException(ErrorCode.USER_NAME_GET_FAIL);
        return ResponseEntity.ok().body(ApiResponse.success(nickName));
    }

    @PostMapping("/info/profile")
    public ResponseEntity<Resource> updateUserInfo( HttpServletRequest request,
                                                         @RequestParam MultipartFile image ){
        Long userId = extractAndValidateUserId(request);

        String key;
        try {
            key = s3Service.uploadObject(image);
            userService.updateProfile(userId, key);

            Resource resource = new InputStreamResource(image.getInputStream());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(image.getContentType()));
            headers.setContentLength(image.getSize());
            headers.setContentDisposition(ContentDisposition.inline().filename(image.getOriginalFilename()).build());

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (Exception e){
            throw new CustomException(ErrorCode.USER_IMAGE_UPLOAD_FAIL);
        }
    }

    @PostMapping("/info/nickname")
    public ResponseEntity<ApiResponse<?>> updateUserInfo( HttpServletRequest request,
                                                         @RequestBody NicknameDto nickName ){
        Long userId = extractAndValidateUserId(request);
        String newNickName = userService.updateUsername(userId, nickName.getNickName());
        return ResponseEntity.ok().body(ApiResponse.success(new NicknameDto(newNickName)));
    }

    @GetMapping("/photos")
    public ResponseEntity<ApiResponse<?>> getUserPhotos(HttpServletRequest request){
        Long userId = extractAndValidateUserId(request);
        List<uploadPhotoDto> photosByUser = userService.getPhotosByUser(userId);
        return ResponseEntity.ok().body(ApiResponse.success(photosByUser));
    }

    @GetMapping("/likes")
    public ResponseEntity<ApiResponse<?>> getUserLikes(HttpServletRequest request){
        Long userId = extractAndValidateUserId(request);
        List<uploadPhotoDto> LikePhotosByUser = userService.getLikedPhotosByUser(userId);
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
