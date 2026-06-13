package com.app.backend.domain.auth.oauth;

public record OAuthUserInfo(
        String providerId,
        String email,
        String nickname,
        String profileImageUrl
) {
}
