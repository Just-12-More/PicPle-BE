package com.Just_112_More.PicPle.stat.dto;

import com.Just_112_More.PicPle.photo.dto.uploadPhotoDto;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@EqualsAndHashCode
public class HotPlaceResponse {
    private int order;
    private String locationLabel;
    private int photoCnt;
    private String latitude;
    private String longitude;
}
