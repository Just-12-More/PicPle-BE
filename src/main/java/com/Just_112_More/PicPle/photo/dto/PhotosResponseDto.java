package com.Just_112_More.PicPle.photo.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
public class PhotosResponseDto {
    private final List<uploadPhotoDto> photos;

    @Builder
    public PhotosResponseDto(List<uploadPhotoDto> photos) {
        this.photos = photos;
    }
}

