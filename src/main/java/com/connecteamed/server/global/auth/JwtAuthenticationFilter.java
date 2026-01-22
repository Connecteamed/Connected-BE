package com.connecteamed.server.global.auth;
import com.connecteamed.server.domain.token.repository.BlacklistedTokenRepository;
import com.connecteamed.server.global.apiPayload.code.BaseErrorCode;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;
import com.connecteamed.server.global.auth.exception.code.AuthErrorCode;
import com.connecteamed.server.global.util.FilterResponseUtils;
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
    private final FilterResponseUtils filterResponseUtils;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. 헤더에서 토큰 추출
        String authorization = request.getHeader("Authorization");

        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);

            try {
                // 1. 블랙리스트 체크
                if (blacklistedTokenRepository.existsByToken(token)) {
                    throw new GeneralException(AuthErrorCode.INVALID_TOKEN); // 혹은 별도 에러코드
                }

                // 2. 상세 검증-> 문제시 예외 throw 지점
                jwtUtil.validateToken(token);

                // 3. 인증 처리
                String loginId = jwtUtil.getUserId(token);
                CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(loginId);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (GeneralException e) {
                // 필터에서 직접 JSON 응답 생성
                filterResponseUtils.sendErrorResponse(response, e.getCode());
                return;
            }
        }

        //  다음 필터로 전달 (에러가 났던 지점!)
        filterChain.doFilter(request, response);
    }


}
