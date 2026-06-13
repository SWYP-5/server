package com.app.backend.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_OAUTH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 소셜 로그인 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 refresh token 입니다."),
    UNDER_MIN_AGE(HttpStatus.FORBIDDEN, "만 14세 미만은 가입할 수 없습니다."),
    TERMS_NOT_AGREED(HttpStatus.BAD_REQUEST, "약관에 동의해야 합니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}