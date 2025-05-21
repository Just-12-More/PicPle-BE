package com.Just_112_More.PicPle.user.service;

import com.Just_112_More.PicPle.security.jwt.JwtUtil;
import com.Just_112_More.PicPle.security.oauth.OAuth2TokenVerifier;
import com.Just_112_More.PicPle.security.oauth.OAuth2VerifierFactory;
import com.Just_112_More.PicPle.security.oauth.OAuthUserInfo;
import com.Just_112_More.PicPle.security.principal.UserPrincipal;
import com.Just_112_More.PicPle.user.domain.User;
import com.Just_112_More.PicPle.user.dto.LoginRequest;
import com.Just_112_More.PicPle.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OAuth2VerifierFactory verifierFactory;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public String loginViaOAuth(LoginRequest request) {
        OAuth2TokenVerifier verifier = verifierFactory.getVerifier(request.getProvider());
        OAuthUserInfo userInfo = verifier.verify(request.getAccessToken());

        User user = userRepository.findByProviderAndProviderId(userInfo.getProvider(), userInfo.getProviderId())
                .orElseGet(() -> userRepository.save(User.fromOAuth(userInfo)));

        UserPrincipal principal = new UserPrincipal(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );
        return jwtUtil.createToken(authentication);
    }
}
