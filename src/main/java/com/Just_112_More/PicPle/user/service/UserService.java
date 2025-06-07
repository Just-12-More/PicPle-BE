package com.Just_112_More.PicPle.user.service;

import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.Just_112_More.PicPle.like.domain.Like;
import com.Just_112_More.PicPle.like.repository.LikeRepository;
import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.photo.dto.MyPagePhotoDto;
import com.Just_112_More.PicPle.photo.dto.uploadPhotoDto;
import com.Just_112_More.PicPle.user.domain.User;
import com.Just_112_More.PicPle.user.dto.NicknameDto;
import com.Just_112_More.PicPle.user.dto.ProfileDto;
import com.Just_112_More.PicPle.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    // 사용자 정보 조회( 닉네임, 프사 )
    public ProfileDto getUsernameAndProfile(Long userId){
        ProfileDto profileDto = userRepository.findUsernameAndProfile(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_INFO_NOT_FOUND));

        // 프로필 이미지 비어있는 경우
        if (profileDto.getProfilePath() == null || profileDto.getProfilePath().isEmpty()) {
            profileDto.setProfilePath("profile/default-profile.jpeg");
        }

        String fullProfileUrl = "https://picple-pictures.s3.ap-northeast-2.amazonaws.com/" + profileDto.getProfilePath();
        profileDto.setProfilePath(fullProfileUrl);
        return profileDto;
    }

    // 사용자 정보 수정( 프사 )
    @Transactional
    public ProfileDto updateProfile(Long userId, String newProfileImage, String newNickName) {
        User user = userRepository.findOne(userId)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        ProfileDto profileDto = new ProfileDto();

        log.info("수정전 : {} & {}", user.getUserName(), user.getProfilePath());
        if (newProfileImage != null && !newProfileImage.equals(user.getProfilePath())) {
            user.setProfilePath(newProfileImage);  // 프로필 사진 경로 수정
        }
        if (newNickName != null && !newNickName.equals(user.getUserName())) {
            user.setUserName(newNickName);  // 닉네임 수정
        }
        log.info("수정후 : {} & {}", user.getUserName(), user.getProfilePath());

        profileDto.setUsername(user.getUserName());
        if(user.getProfilePath()==null) {
            profileDto.setProfilePath("profile/default-profile.jpeg");
        } else {
            profileDto.setProfilePath(user.getProfilePath());
        }
        String fullProfileUrl = "https://picple-pictures.s3.ap-northeast-2.amazonaws.com/" + profileDto.getProfilePath();
        profileDto.setProfilePath(fullProfileUrl);
        return profileDto;
    }

    // 사용자가 업로드한 사진 목록 조회
    public List<MyPagePhotoDto> getPhotosByUser(Long userId) {
        User user = userRepository.findOne(userId)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        return user.getUserPhotos().stream()
                .map(photo -> new MyPagePhotoDto(
                        photo.getId(),
                        "https://picple-pictures.s3.ap-northeast-2.amazonaws.com/" + photo.getPhotoUrl()
                ))
                .collect(Collectors.toList());
    }

    // 사용자가 좋아요한 사진 목록 조회
    public List<MyPagePhotoDto> getLikedPhotosByUser(Long userId) {
        User user = userRepository.findOne(userId)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Like> likePhotos = likeRepository.findLikesWithPhotoByUserId(userId);

        return likePhotos.stream()
                .map(like -> {
                    Photo photo = like.getPhoto();
                    return new MyPagePhotoDto(
                            photo.getId(),
                            "https://picple-pictures.s3.ap-northeast-2.amazonaws.com/" + photo.getPhotoUrl()
                    );
                })
                .collect(Collectors.toList());
    }

    // 탈퇴여부 체킹
    public Long validateUserId(Long userId){
        return userRepository.findeByIdAndIsDeletedFalse(userId)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND_OR_DELETED));
    }
}