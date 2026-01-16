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
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

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



    // local: API 전체 오픈
    @Bean
    @Profile("local")
    public SecurityFilterChain localFilterChain(HttpSecurity http) throws Exception {
        configureCommonSecurity(http);
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/index.html", "/document.html",
                    "/css/**", "/js/**", "/images/**", "/favicon.ico", "/error",
                    "/docs", "/docs/**", "/swagger-ui/**", "/v3/api-docs/**"
                ).permitAll()
                .requestMatchers("/api/**").permitAll()
                .anyRequest().permitAll()
            );

        return http.build();
    }

    // dev/prod: API 보호
    @Bean
    @Profile("!local")
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        configureCommonSecurity(http);
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/index.html", "/document.html",
                    "/css/**", "/js/**", "/images/**", "/favicon.ico", "/error",
                    "/docs", "/docs/**", "/swagger-ui/**", "/v3/api-docs/**"
                ).permitAll()
                // 로그인/회원가입 같은 것만 예외로 오픈
                .requestMatchers("/api/auth/**","/api/member/signup","/api/members/check-id","/api/auth/refresh").permitAll()
                .anyRequest().authenticated()
                )
                // JWT 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, customUserDetailsService, blacklistedTokenRepository),
                        org.springframework.security.web.authentication.logout.LogoutFilter.class)
                // 상세 로그아웃 설정
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .addLogoutHandler(jwtLogoutHandler)
                        .logoutSuccessHandler(customLogoutSuccessHandler())
                );

        return http.build();
    }

    //공통 설정
    private void configureCommonSecurity(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 무조건 Stateless 유지
                );
    }


    //로그아웃 성공 핸들러
    private LogoutSuccessHandler customLogoutSuccessHandler() {
        return (request, response, authentication) -> {
            response.setContentType("application/json;charset=UTF-8");
            ObjectMapper objectMapper = new ObjectMapper();

            if (authentication == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.onFailure(AuthErrorCode.TOKEN_EXPIRED)));
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.onSuccess(AuthSuccessCode.LOGOUT_SUCCESS, null)));
        };
    }
}
