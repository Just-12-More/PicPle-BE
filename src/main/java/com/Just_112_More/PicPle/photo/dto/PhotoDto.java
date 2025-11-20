package com.Just_112_More.PicPle.photo.dto;

import com.Just_112_More.PicPle.photo.domain.Photo;
import com.Just_112_More.PicPle.photo.domain.PhotoTag;
import com.Just_112_More.PicPle.photo.domain.Tag;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PhotoDto {
    private final Long id;
    private final String imgUrl;
    private final List<String> tags;

    public PhotoDto(Photo photo, String s3Url) {
        this.id = photo.getId();
        this.imgUrl = s3Url + photo.getPhotoUrl();
        this.tags = photo.getPhotoTags().stream()
                .map(PhotoTag::getTag)
                .map(Tag::getName)
                .collect(Collectors.toList());
    }
}