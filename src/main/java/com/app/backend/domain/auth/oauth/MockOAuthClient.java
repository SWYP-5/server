package com.app.backend.domain.auth.oauth;

import com.app.backend.domain.user.entity.AuthProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class MockOAuthClient implements OAuthClient {

    @Override
    public AuthProvider getProvider() {
        return AuthProvider.KAKAO;
    }

    @Override
    public OAuthUserInfo getUserInfo(String accessToken) {
        return new OAuthUserInfo("mock-" + accessToken);
    }
}