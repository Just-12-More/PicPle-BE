package com.Just_112_More.PicPle.photo.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
public class photosResponseDto {
    private final List<uploadPhotoDto> photos;

    @Builder
    public photosResponseDto(List<uploadPhotoDto> photos) {
        this.photos = photos;
    }
}

