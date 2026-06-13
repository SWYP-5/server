package com.app.backend.domain.auth.service;

import com.app.backend.domain.auth.dto.AuthResponse;
import com.app.backend.domain.auth.dto.SignupRequest;
import com.app.backend.domain.auth.dto.SocialLoginRequest;
import com.app.backend.domain.auth.dto.TokenResponse;
import com.app.backend.domain.auth.entity.RefreshToken;
import com.app.backend.domain.auth.jwt.JwtProvider;
import com.app.backend.domain.auth.oauth.OAuthClient;
import com.app.backend.domain.auth.oauth.OAuthUserInfo;
import com.app.backend.domain.auth.repository.RefreshTokenRepository;
import com.app.backend.domain.user.entity.AuthProvider;
import com.app.backend.domain.user.entity.User;
import com.app.backend.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;

@Service
public class AuthService {

    private static final int MIN_AGE = 14;

    private final OAuthClient oAuthClient;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenExpiration;

    public AuthService(OAuthClient oAuthClient,
                       UserRepository userRepository,
                       JwtProvider jwtProvider,
                       RefreshTokenRepository refreshTokenRepository,
                       @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration) {
        this.oAuthClient = oAuthClient;
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    @Transactional
    public AuthResponse login(AuthProvider provider, SocialLoginRequest request) {
        OAuthUserInfo userInfo = oAuthClient.getUserInfo(request.accessToken());

        Optional<User> found =
                userRepository.findByProviderAndProviderId(provider, userInfo.providerId());

        if (found.isEmpty()) {
            return AuthResponse.signupRequired();
        }

        return issueTokens(found.get(), false);
    }

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        OAuthUserInfo userInfo = oAuthClient.getUserInfo(request.accessToken());

        Optional<User> found =
                userRepository.findByProviderAndProviderId(request.provider(), userInfo.providerId());
        if (found.isPresent()) {
            return issueTokens(found.get(), true);
        }

        if (isUnderMinAge(request.birthDate())) {
            throw new IllegalArgumentException("만 14세 미만은 가입할 수 없습니다.");
        }

        User user = userRepository.save(User.builder()
                .provider(request.provider())
                .providerId(userInfo.providerId())
                .email(userInfo.email())
                .nickname(userInfo.nickname())
                .birthDate(request.birthDate())
                .profileImageUrl(userInfo.profileImageUrl())
                .build());

        return issueTokens(user, true);
    }

    @Transactional
    public TokenResponse refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 refresh token 입니다.");
        }

        RefreshToken saved = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 refresh token 입니다."));

        String newAccessToken = jwtProvider.createAccessToken(saved.getUserId());
        return new TokenResponse(newAccessToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.deleteByToken(refreshToken);
    }

    private AuthResponse issueTokens(User user, boolean isNewUser) {
        String accessToken = jwtProvider.createAccessToken(user.getId());
        String refreshToken = jwtProvider.createRefreshToken(user.getId());

        refreshTokenRepository.save(RefreshToken.builder()
                .token(refreshToken)
                .userId(user.getId())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000))
                .build());

        return isNewUser
                ? AuthResponse.signup(accessToken, refreshToken, user)
                : AuthResponse.login(accessToken, refreshToken, user);
    }

    private boolean isUnderMinAge(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears() < MIN_AGE;
    }
}