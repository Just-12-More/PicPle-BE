package com.Just_112_More.PicPle.photo.dto;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class NearbyPhotosResponseDto {
    private String address;
    private uploadPhotoDto centerPhoto;
    private List<uploadPhotoDto> nearbyPhotos;
}