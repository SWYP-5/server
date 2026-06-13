package com.app.backend.domain.auth.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private final JwtProvider jwtProvider = new JwtProvider(
            "test-secret-key-must-be-at-least-32-characters-long",
            900000L,      // access 15분
            2592000000L   // refresh 30일
    );

    @Test
    @DisplayName("access token을 만들고 다시 까면 userId가 그대로 나온다")
    void createAccessToken_thenGetUserId() {
        // 준비
        Long userId = 5L;

        // 실행
        String token = jwtProvider.createAccessToken(userId);
        Long extracted = jwtProvider.getUserId(token);

        // 검증
        assertThat(extracted).isEqualTo(userId);
    }

    @Test
    @DisplayName("정상 토큰은 validateToken이 true를 반환한다")
    void validateToken_validToken_returnsTrue() {
        String token = jwtProvider.createAccessToken(5L);

        assertThat(jwtProvider.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("위조된 토큰은 validateToken이 false를 반환한다")
    void validateToken_invalidToken_returnsFalse() {
        String fakeToken = "this.is.not.a.valid.token";

        assertThat(jwtProvider.validateToken(fakeToken)).isFalse();
    }

    @Test
    @DisplayName("refresh token도 만들고 까면 userId가 나온다")
    void createRefreshToken_thenGetUserId() {
        Long userId = 42L;

        String token = jwtProvider.createRefreshToken(userId);

        assertThat(jwtProvider.getUserId(token)).isEqualTo(userId);
    }
}
