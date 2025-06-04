package com.Just_112_More.PicPle.user.service;

import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.Just_112_More.PicPle.like.domain.Like;
import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.user.domain.User;
import com.Just_112_More.PicPle.user.dto.ProfileDto;
import com.Just_112_More.PicPle.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
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

        String defautImage = "";
        if (profileDto.getProfileURL() == null || profileDto.getProfileURL().isEmpty()) {
            profileDto.setProfileURL(defautImage);
        }
        return profileDto;
    }

    // 사용자 정보 수정( 닉네임, 프사 )
    @Transactional
    public void updateUserInfo(Long userId, String newNickname, String newProfileImage) {
        User user = userRepository.findOne(userId).orElse(null);
        if (user != null) {
            user.setUserName(newNickname);  // 닉네임 수정
            user.setProfileUrl(newProfileImage);  // 프로필 사진 수정
            userRepository.save(user);  // 수정된 정보 저장
        }
    }

    // 회원탈퇴 ( 논리적 삭제 )
    @Transactional
    public void deactivateUser(Long userId) {
        User user = userRepository.findOne(userId).orElse(null);
        if (user != null) {
            user.deleteUser();  // 논리 삭제 처리
            userRepository.save(user);  // 변경된 사용자 상태 저장
        }
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
}