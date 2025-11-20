package com.Just_112_More.PicPle.photo.dto;

import com.Just_112_More.PicPle.photo.domain.Tag;
import lombok.Getter;

@Getter
public class HotTagDto extends TagDto {
    private final Integer count;

    public HotTagDto(Tag tag, Integer count) {
        super(tag);
        this.count = count;
    }
}
