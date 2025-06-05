package com.Just_112_More.PicPle.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

@Getter
@Setter
@AllArgsConstructor
public class ProfileWithImageDto {
    private String username;
    //private byte[] profileImageBytes;
    private Resource profileImage;

}
