package com.Just_112_More.PicPle.security.oauth;

import com.Just_112_More.PicPle.exception.CustomException;
import com.Just_112_More.PicPle.exception.ErrorCode;
import com.Just_112_More.PicPle.user.domain.LoginProvider;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class KakaoTokenVerifier implements OAuth2TokenVerifier {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuthUserInfo verify(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                throw new CustomException(ErrorCode.INVALID_OAUTH_TOKEN);
            }

            Map<String, Object> body = response.getBody();
            if (body == null || !body.containsKey("id")) {
                throw new CustomException(ErrorCode.OAUTH_RESPONSE_INVALID);
            }

            // 사용자 ID는 반드시 존재하므로, 식별자로 사용
            String id = String.valueOf(body.get("id"));

            // kakao_account는 optional
            Map<String, Object> kakaoAccount = (Map<String, Object>) body.get("kakao_account");
            String email = null;
            if (kakaoAccount != null) {
                email = (String) kakaoAccount.get("email");
            }

            return new OAuthUserInfo(id, email, LoginProvider.KAKAO);
        } catch ( RestClientException e ) {
            throw new CustomException(ErrorCode.OAUTH_SERVER_ERROR);  // 카카오 서버 문제 or 연결 문제
        }
    }

    @Override
    public LoginProvider getProvider() {
        return LoginProvider.KAKAO;
    }
}
