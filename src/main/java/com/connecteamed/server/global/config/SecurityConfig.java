package com.connecteamed.server.global.config;

import com.connecteamed.server.domain.token.repository.BlacklistedTokenRepository;
import com.connecteamed.server.global.apiPayload.ApiResponse;
import com.connecteamed.server.global.auth.JwtAuthenticationFilter;
import com.connecteamed.server.global.auth.JwtLogoutHandler;
import com.connecteamed.server.global.auth.JwtUtil;
import com.connecteamed.server.global.auth.CustomUserDetailsService;
import com.connecteamed.server.global.auth.exception.code.AuthErrorCode;
import com.connecteamed.server.global.auth.exception.code.AuthSuccessCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // JwtUtil과 Service 주입을 위해 필요합니다.
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    private final JwtLogoutHandler jwtLogoutHandler;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 및 기본 설정 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 2. 세션을 사용하지 않음 (JWT를 사용하기 때문)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. 권한 설정
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/docs", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/auth/logout").authenticated()
                        .requestMatchers("/api/auth/**").permitAll() // 로그인, 회원가입 경로는 허용
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/api/member/signup").permitAll()
                        .anyRequest().authenticated()
                )

                // 4. JWT 필터를 ID/PW 인증 필터보다 앞에 배치
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, customUserDetailsService, blacklistedTokenRepository),
                        org.springframework.security.web.authentication.logout.LogoutFilter.class)

                //로그아웃 설정
                .logout(logout -> logout
                .logoutUrl("/api/auth/logout") // 로그아웃을 수행할 URL
                .addLogoutHandler(jwtLogoutHandler) // 우리가 만든 핸들러 추가
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setContentType("application/json;charset=UTF-8");

                    ObjectMapper objectMapper = new ObjectMapper();

                    //  인증 정보가 없다면(토큰이 없거나 무효하다면) 에러 응답 반환
                    if (authentication == null) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 에러
                        // 동현님이 정의한 AuthErrorCode 혹은 적절한 에러 코드를 사용하세요.
                        ApiResponse<Object> errorResponse = ApiResponse.onFailure(AuthErrorCode.TOKEN_EXPIRED);
                        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                        return;
                    }

                    // 인증된 유저인 경우 정상 로그아웃 응답
                    response.setStatus(HttpServletResponse.SC_OK);
                    ApiResponse<String> successResponse = ApiResponse.onSuccess(AuthSuccessCode.LOGOUT_SUCCESS, null);
                    response.getWriter().write(objectMapper.writeValueAsString(successResponse));
                })
        );
        return http.build();
    }
}
