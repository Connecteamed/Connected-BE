package com.connecteamed.server.global.auth;

import com.connecteamed.server.domain.token.entity.BlacklistedToken;
import com.connecteamed.server.domain.token.repository.BlacklistedTokenRepository;
import com.connecteamed.server.domain.token.repository.RefreshTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {

    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String accessToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("Refresh-Token");

        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            String token = accessToken.substring(7);

            // 1. 토큰이 유효한지 먼저 확인
            if (jwtUtil.isValid(token)) {
                Instant expiryDate = jwtUtil.getExpiryDate(token);

                // 2. 이미 블랙리스트에 있는지 확인 후 저장
                if (!blacklistedTokenRepository.existsByToken(token)) {
                    blacklistedTokenRepository.save(new BlacklistedToken(token, expiryDate));
                }
            }
        }

        if (refreshToken != null) {
            refreshTokenRepository.deleteByToken(refreshToken);
        }
    }
}
