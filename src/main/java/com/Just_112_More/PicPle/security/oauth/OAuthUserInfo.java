package com.Just_112_More.PicPle.security.oauth;

import com.Just_112_More.PicPle.user.domain.LoginProvider;
import lombok.Getter;

@Getter
public class OAuthUserInfo {

    public OAuthUserInfo(String providerId, String email, LoginProvider provider) {
        this.providerId = providerId;
        this.email = email;
        this.provider = provider;
    }

    private String providerId; // ex: kakao의 유저 ID
    private String email;
    private LoginProvider provider;
}
