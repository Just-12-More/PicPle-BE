package com.Just_112_More.PicPle.photo.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class RecommendRequest {
    private List<Long> adjectiveTagIds;
    private List<Long> nounTagIds;
}
