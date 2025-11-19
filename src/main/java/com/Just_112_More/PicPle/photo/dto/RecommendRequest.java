package com.Just_112_More.PicPle.photo.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RecommendRequest {
    private List<Long> tagIds;
}
