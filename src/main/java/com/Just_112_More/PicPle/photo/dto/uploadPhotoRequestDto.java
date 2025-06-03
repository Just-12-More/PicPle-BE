package com.Just_112_More.PicPle.photo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class uploadPhotoRequestDto {
    private String title;
    private String description;
    private String photoUrl;
    private Double latitude;
    private Double longitude;
}
