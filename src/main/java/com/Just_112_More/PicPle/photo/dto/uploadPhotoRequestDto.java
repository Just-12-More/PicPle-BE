package com.Just_112_More.PicPle.photo.dto;

import lombok.Getter;

@Getter
public class uploadPhotoRequestDto {
    private String title;
    private String description;
    private String photoUrl;
    private Double latitude;
    private Double longitude;
}
