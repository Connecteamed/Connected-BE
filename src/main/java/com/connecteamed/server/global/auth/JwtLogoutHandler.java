package com.connecteamed.server.global.auth;

import com.connecteamed.server.domain.token.entity.BlacklistedToken;
import com.connecteamed.server.domain.token.repository.BlacklistedTokenRepository;
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
    private final JwtUtil jwtUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 1. 토큰이 유효한지 먼저 확인
            if (jwtUtil.isValid(token)) {
                Instant expiryDate = jwtUtil.getExpiryDate(token);

                // 2. 이미 블랙리스트에 있는지 확인 후 저장
                if (!blacklistedTokenRepository.existsByToken(token)) {
                    blacklistedTokenRepository.save(new BlacklistedToken(token, expiryDate));
                }
            }
        }
    }
}
