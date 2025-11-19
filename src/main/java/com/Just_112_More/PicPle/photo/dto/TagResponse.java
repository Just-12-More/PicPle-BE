package com.Just_112_More.PicPle.photo.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class TagResponse {
    private final List<TagDto> adjectives;
    private final List<TagDto> nouns;

    public TagResponse(List<TagDto> adjectives, List<TagDto> nouns) {
        this.adjectives = adjectives;
        this.nouns = nouns;
    }
}
