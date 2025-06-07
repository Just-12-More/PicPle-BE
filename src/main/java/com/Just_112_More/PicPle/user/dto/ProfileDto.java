package com.Just_112_More.PicPle.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProfileDto {
    private String username;
    private String profilePath;

    public ProfileDto() {}
}
