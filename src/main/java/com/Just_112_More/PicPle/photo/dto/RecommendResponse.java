package com.Just_112_More.PicPle.photo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RecommendResponse {
    List<PhotoDto> photos;
}
