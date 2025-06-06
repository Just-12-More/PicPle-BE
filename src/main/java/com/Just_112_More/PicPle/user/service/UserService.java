package com.Just_112_More.PicPle.user.service;

import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.Just_112_More.PicPle.like.domain.Like;
import com.Just_112_More.PicPle.photo.domain.Photo;
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

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    // 사용자 조회
    public User getUserInfo(Long userId) {
        return userRepository.findOne(userId).orElse(null);
    }

    // 사용자 정보 조회( 닉네임, 프사 )
    public ProfileDto getUsernameAndProfile(Long userId){
        ProfileDto profileDto = userRepository.findUsernameAndProfile(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 프로필 이미지 비어있는 경우
        if (profileDto.getProfilePath() == null || profileDto.getProfilePath().isEmpty()) {
            profileDto.setProfilePath("profile/default-profile.jpeg");
        }
        return profileDto;
    }

    // 사용자 정보 수정( 프사 )
    @Transactional
    public void updateProfile(Long userId, String newProfileImage) {
        User user = userRepository.findOne(userId)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        if ( user.getProfilePath()==null || !user.getProfilePath().equals(newProfileImage)) user.setProfilePath(newProfileImage);
        //userRepository.save(user);  // 수정된 정보 저장
    }

    // 사용자 정보 수정( 닉네임 )
    @Transactional
    public String updateUsername(Long userId, String newNickname) {
        User user = userRepository.findOne(userId)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND));

        log.info("기존 닉네임: {}", user.getUserName());
        if (user.getUserName()==null || !user.getUserName().equals(newNickname)) {
            user.setUserName(newNickname);
        }
        //User updateUser = userRepository.save(user);// 수정된 정보 저장
        return user.getUserName();
    }

    // 사용자가 업로드한 사진 목록 조회
    public List<Photo> getPhotosByUser(User user) {
        // User 엔티티의 userPhotos 필드를 사용
        return user.getUserPhotos();
    }

    // 사용자가 좋아요한 사진 목록 조회
    public List<Photo> getLikedPhotosByUser(User user) {
        // User 엔티티의 userLikes 필드를 사용
        List<Like> likes = user.getUserLikes();
        List<Photo> photos = new ArrayList<>();
        for (Like like : likes) {
            photos.add(like.getPhoto());
        }
        return photos;
    }

    public Long validateUserId(Long userId){
        return userRepository.findeByIdAndIsDeletedFalse(userId)
                .orElseThrow(()-> new CustomException(ErrorCode.USER_NOT_FOUND_OR_DELETED));
    }
}