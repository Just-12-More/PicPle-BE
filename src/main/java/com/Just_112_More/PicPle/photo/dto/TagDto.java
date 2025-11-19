package com.Just_112_More.PicPle.photo.dto;

import com.Just_112_More.PicPle.photo.domain.Tag;
import lombok.Getter;

@Getter
public class TagDto {
    private Long id;
    private String name;
    private String tagType;

    public TagDto(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
        this.tagType = tag.getTagType().name();
    }
}
