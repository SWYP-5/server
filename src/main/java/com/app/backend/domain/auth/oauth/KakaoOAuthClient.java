package com.app.backend.domain.auth.oauth;

import com.app.backend.domain.user.entity.AuthProvider;
import com.app.backend.global.exception.CustomException;
import com.app.backend.global.exception.ErrorCode;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@Profile("!local")
public class KakaoOAuthClient implements OAuthClient {

    private static final String USER_INFO_URI = "https://kapi.kakao.com/v2/user/me";

    private final RestClient restClient;

    public KakaoOAuthClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    @Override
    public AuthProvider getProvider() {
        return AuthProvider.KAKAO;
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        KakaoUserResponse response;
        try {
            response = restClient.get()
                    .uri(USER_INFO_URI)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(KakaoUserResponse.class);
        } catch (RestClientException e) {
            throw new CustomException(ErrorCode.INVALID_OAUTH_TOKEN);
        }

        if (response == null || response.id() == null) {
            throw new CustomException(ErrorCode.INVALID_OAUTH_TOKEN);
        }

        return new OAuthUserInfo(String.valueOf(response.id()));
    }

    private record KakaoUserResponse(Long id) {
    }
}