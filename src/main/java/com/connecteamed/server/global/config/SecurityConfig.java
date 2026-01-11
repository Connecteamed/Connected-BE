package com.connecteamed.server.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 테스트를 위해 CSRF 보호 비활성화
                .authorizeHttpRequests(auth -> auth
                        // Swagger UI 및 관련 리소스 접근 허용
                        .requestMatchers("/docs", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // API 경로로 이어지는 접근 허용
                        .requestMatchers("/api/**").permitAll()
                        // 그 외의 요청은 인증 필요하도록 설정
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
