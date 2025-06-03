package com.Just_112_More.PicPle.photo.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class uploadPhotoDto {
    private long id;
    private String title;
    private String imgUrl;
    private String description;
    private String nickname;
    private String profileImgUrl;
    private int likeCount;
    private Boolean isLiked;
    private String address;
    private String createdAt;
    private double latitude;
    private double longitude;

    // 명시적 생성자 (Builder 사용 시 일반적으로 불필요하지만 원하면 추가 가능)
    public uploadPhotoDto(long id, String title, String imgUrl, String description,
                          String nickname, String profileImgUrl, int likeCount,
                          Boolean isLiked, String address, String createdAt) {
        this.id = id;
        this.title = title;
        this.imgUrl = imgUrl;
        this.description = description;
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.likeCount = likeCount;
        this.isLiked = isLiked;
        this.address = address;
        this.createdAt = createdAt;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
