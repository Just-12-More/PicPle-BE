package com.Just_112_More.PicPle.photo.dto;

import lombok.Builder;

public class GetS3UrlDto {
    private String preSignedUrl;
    private String key;

    @Builder
    public GetS3UrlDto(String preSignedUrl, String key) {
        this.preSignedUrl = preSignedUrl;
        this.key = key;
    }
}
