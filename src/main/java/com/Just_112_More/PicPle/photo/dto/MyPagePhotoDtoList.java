package com.Just_112_More.PicPle.photo.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MyPagePhotoDtoList {
    private final List<MyPagePhotoDto> photos;

    @Builder
    public MyPagePhotoDtoList(List<MyPagePhotoDto> photos) {
        this.photos = photos;
    }
}
