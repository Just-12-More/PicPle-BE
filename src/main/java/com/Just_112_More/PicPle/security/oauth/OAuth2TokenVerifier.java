package com.Just_112_More.PicPle.security.oauth;

import com.Just_112_More.PicPle.user.domain.LoginProvider;

public interface OAuth2TokenVerifier {

    OAuthUserInfo verify(String accessToken);
    LoginProvider getProvider();
}
