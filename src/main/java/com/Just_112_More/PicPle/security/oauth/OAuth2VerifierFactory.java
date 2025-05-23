package com.Just_112_More.PicPle.security.oauth;

import com.Just_112_More.PicPle.user.domain.LoginProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OAuth2VerifierFactory {
    private final Map<LoginProvider, OAuth2TokenVerifier> verifierMap;

    public OAuth2VerifierFactory(List<OAuth2TokenVerifier> verifiers) {
        this.verifierMap = verifiers.stream()
                .collect(Collectors.toMap(OAuth2TokenVerifier::getProvider, v -> v));
    }

    public OAuth2TokenVerifier getVerifier(LoginProvider provider) {
        return verifierMap.get(provider);
    }
}