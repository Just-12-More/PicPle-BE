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

    @Value("${urls.s3}")
    private String s3Url;

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
                    .imgUrl(s3Url + photo.getPhotoUrl())
                    .description(photo.getPhotoDesc())
                    .nickname(user.getUserName())
                    .profileImgUrl(user.getProfileUrl())
                    .likeCount(photo.getLikeCount())
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

    //인접 반경 사진들 반환
    @PostMapping("/search/location")
    public ResponseEntity<ApiResponse<?>> getPhotosInRadius(
            @RequestBody photoSearchRequestDto requestDto) {
        List<Photo> photos = photoRepository.findPhotosByLocation(
                requestDto.getLatitude(), requestDto.getLongitude(), requestDto.getRadius());

        List<uploadPhotoDto> dtoList = photos.stream()
                .map(photo -> uploadPhotoDto.builder()
                        .id(photo.getId())
                        .title(photo.getPhotoTitle())
                        .imgUrl(s3Url + photo.getPhotoUrl())
                        .description(photo.getPhotoDesc())
                .nickname(photo.getUser().getUserName())
                .profileImgUrl(photo.getUser().getProfileUrl())
                .likeCount(photo.getLikeCount())
                .isLiked(false)
                .address(photo.getLocationLabel())
                .createdAt(photo.getPhotoCreate().toString())
                .latitude(photo.getLatitude())
                .longitude(photo.getLongitude())
                .build())
                .toList();

        photosResponseDto responseDto = photosResponseDto.builder()
                .photos(dtoList)
                .build();

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    //현재 위치 사진들 반환
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<?>> getNearbyPhotos(
            HttpServletRequest request,
            @RequestParam long photo_id
    ) {
        // 사진을 찾기
        Photo centerPhoto = photoRepository.getPhotoById(photo_id);
        if (centerPhoto == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(null, "PHOTO_NOT_FOUND", "사진을 찾을 수 없습니다."));
        }

        // 주어진 사진의 주변 사진들 찾기
        List<Photo> nearbyPhotos = photoRepository.findPhotosByLocation(centerPhoto.getLatitude(), centerPhoto.getLongitude(), photo_id);

        // 센터 사진 DTO 생성
       uploadPhotoDto centerPhotoDto = uploadPhotoDto.builder()
                .id(centerPhoto.getId())
                .title(centerPhoto.getPhotoTitle())
                .imgUrl(s3Url + centerPhoto.getPhotoUrl())
                .description(centerPhoto.getPhotoDesc())
                .nickname(centerPhoto.getUser().getUserName())
                .profileImgUrl(centerPhoto.getUser().getProfileUrl())
                .likeCount(centerPhoto.getLikeCount())
                .isLiked(false) // 실제로 로그인한 사용자가 있으면 여기서 체크
                .address(centerPhoto.getLocationLabel())
                .createdAt(centerPhoto.getPhotoCreate().toString())
                .build();

        // 주변 사진 DTO 생성
        List<uploadPhotoDto> nearbyPhotoDtos = nearbyPhotos.stream()
                .map(photo -> uploadPhotoDto.builder()
                        .id(photo.getId())
                        .title(photo.getPhotoTitle())
                        .imgUrl(s3Url + photo.getPhotoUrl())
                        .description(photo.getPhotoDesc())
                        .nickname(photo.getUser().getUserName())
                        .profileImgUrl(photo.getUser().getProfileUrl())
                        .likeCount(photo.getLikeCount())
                        .isLiked(false) // 실제로 로그인한 사용자가 있으면 여기서 체크
                        .address(photo.getLocationLabel())
                        .createdAt(photo.getPhotoCreate().toString())
                        .build())
                .collect(Collectors.toList());

        // 응답 DTO 생성
        NearbyPhotosResponseDto responseDto = NearbyPhotosResponseDto.builder()
                .address(centerPhoto.getLocationLabel())
                .centerPhoto(centerPhotoDto)
                .nearbyPhotos(nearbyPhotoDtos)
                .build();

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }


    //상세조회
    @GetMapping("/details")
    public ResponseEntity<ApiResponse<?>> getPhotoDetails(
            HttpServletRequest request,
            @RequestParam long photo_id
    ) {
    Photo photo = photoRepository.getPhotoById(photo_id);
    User user = photo.getUser();

    if (photo != null) {
            uploadPhotoDto dto = uploadPhotoDto.builder()
                    .id(photo.getId())
                    .title(photo.getPhotoTitle())
                    .imgUrl(s3Url + photo.getPhotoUrl())
                    .description(photo.getPhotoDesc())
                    .nickname(user.getUserName())
                    .profileImgUrl(user.getProfileUrl())
                    .likeCount(photo.getLikeCount())
                    .isLiked(false)
                    .address(photo.getLocationLabel())
                    .createdAt(photo.getPhotoCreate().toString())
                    .build();

            return ResponseEntity.ok(ApiResponse.success(dto));
    } else {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(null, "INTERNAL_ERROR", "요청하신 사진이 없습니다."));
        }
    }
}
