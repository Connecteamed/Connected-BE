package com.connecteamed.server.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 개발/테스트 편의용(운영에서는 정책에 맞게 재설정)
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        // 기본/정적 리소스
                        .requestMatchers(
                                "/", "/index.html",
                                "/document.html",
                                "/member_role.html",
                                "/css/**", "/js/**", "/images/**",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()

                        // Swagger (springdoc)
                        // swagger-ui.path=/docs 라면 /docs, /docs/** 둘 다 열어주는게 안전합니다.
                        .requestMatchers(
                                "/docs", "/docs/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // API (테스트용 전체 오픈)
                        .requestMatchers("/api/**").permitAll()

                        // 그 외는 인증 필요
                        .anyRequest().authenticated()
                )

                // 리다이렉트/팝업 방지용(필요 없으면 제거 가능)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
