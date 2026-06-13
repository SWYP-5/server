package com.app.backend.domain.auth.oauth;

import com.app.backend.domain.user.entity.AuthProvider;

public interface OAuthClient {

    AuthProvider getProvider();

    OAuthUserInfo getUserInfo(String accessToken);
}
