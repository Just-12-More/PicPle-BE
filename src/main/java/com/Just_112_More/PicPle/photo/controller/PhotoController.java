package com.Just_112_More.PicPle.photo.controller;

import com.Just_112_More.PicPle.common.ApiResponse;
import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.photo.dto.uploadPhotoDto;
import com.Just_112_More.PicPle.photo.dto.uploadPhotoRequestDto;
import com.Just_112_More.PicPle.photo.repository.PhotoRepository;
import com.Just_112_More.PicPle.photo.service.PhotoService;
import com.Just_112_More.PicPle.security.jwt.JwtUtil;
import com.Just_112_More.PicPle.user.domain.User;
import com.Just_112_More.PicPle.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/photos")
public class PhotoController {

    private final PhotoRepository photoRepository;
    private final PhotoService photoService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<?>> upload(
            HttpServletRequest request,
            @RequestBody uploadPhotoRequestDto requestDto
    ) {
        try {
            String token = jwtUtil.resolveToken(request);
            if (token == null) throw new CustomException(ErrorCode.ACCESS_TOKEN_MISSING);
            jwtUtil.validateAccessToken(token);
            Long userId = jwtUtil.extractUserId(token, false);

            User user = userRepository.findOne(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            String address = photoService.geoCoding(requestDto.getLatitude(), requestDto.getLongitude());
            //String address ="";
            Photo photo = Photo.builder()
                    .photoTitle(requestDto.getTitle())
                    .photoDesc(requestDto.getDescription())
                    .photoUrl(requestDto.getPhotoUrl())
                    .latitude(requestDto.getLatitude())
                    .longitude(requestDto.getLongitude())
                    .locationLabel(address)
                    .build();
            photo.setUser(user);
            photoRepository.save(photo);

            uploadPhotoDto dto = uploadPhotoDto.builder()
                    .id(photo.getId())
                    .title(photo.getPhotoTitle())
                    .imgUrl(photo.getPhotoUrl())
                    .description(photo.getPhotoDesc())
                    .nickname(user.getUserName())
                    .profileImgUrl(user.getProfileUrl())
                    .likeCount(String.valueOf(photo.getLikeCount()))
                    .isLiked(false)
                    .address(photo.getLocationLabel())
                    .createdAt(photo.getPhotoCreate().toString())
                    .build();

            return ResponseEntity.ok(ApiResponse.success(dto));
        } catch (CustomException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.fail(null, e.getErrorCode().name(), e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.fail(null, "INTERNAL_ERROR", e.getMessage()));
        }
    }
}
