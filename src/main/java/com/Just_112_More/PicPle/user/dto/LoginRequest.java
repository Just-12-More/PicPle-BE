package com.Just_112_More.PicPle.user.dto;

import com.Just_112_More.PicPle.user.domain.LoginProvider;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    private String accessToken;
    private LoginProvider provider; // enum (KAKAO, APPLE 등)
}
