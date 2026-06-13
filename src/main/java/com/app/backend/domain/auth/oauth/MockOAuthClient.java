package com.app.backend.domain.auth.oauth;

import com.app.backend.domain.user.entity.AuthProvider;
import org.springframework.stereotype.Component;

@Component
public class MockOAuthClient implements OAuthClient {

    @Override
    public AuthProvider getProvider() {
        return AuthProvider.KAKAO;
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        return new OAuthUserInfo(
                "mock-" + accessToken,
                "mock@example.com",
                "테스트유저",
                null
        );
    }
}