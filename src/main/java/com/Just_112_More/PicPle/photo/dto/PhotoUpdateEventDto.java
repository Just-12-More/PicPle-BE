package com.Just_112_More.PicPle.photo.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class PhotoUpdateEventDto {
    private String order;
    private String locationLabel;
    private int photoCnt;
    private String longitude;
    private String latitude;
    private String imgUrl;
}
