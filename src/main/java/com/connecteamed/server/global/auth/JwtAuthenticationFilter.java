package com.connecteamed.server.global.auth;
import com.connecteamed.server.domain.token.repository.BlacklistedTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException { // [체크] throws와 파라미터 타입을 정확히 맞춰야 합니다.

        // 1. 헤더에서 토큰 추출
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);

            // 2. 토큰 유효성 검사 (JwtTokenProvider의 메서드명 확인 필요)
            {
            if (jwtUtil.isValid(token)) {

                // 블랙리스트에 있는지 확인
                if (blacklistedTokenRepository.existsByToken(token)) {
                    //블랙리스트에 있을 경우 에러로직 처리 필요
                    return;
                }


                String loginId = jwtUtil.getUserId(token);

                // 3. 유저 정보 조회 및 인증 객체 생성
                CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(loginId);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        }

        // 4. 다음 필터로 전달 (에러가 났던 지점!)
        filterChain.doFilter(request, response);
    }
}
