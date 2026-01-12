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
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())

            .authorizeHttpRequests(auth -> auth
                // 정적 페이지 (resources/static)
                .requestMatchers(
                    "/", "/index.html",
                    "/document.html",
                    "/css/**", "/js/**", "/images/**",
                    "/favicon.ico",
                    "/error"
                ).permitAll()

                // 문서 API 전부 허용(테스트용)
                .requestMatchers("/api/**").permitAll()

                // 혹은 문서만 열고 싶으면 예시처럼 세분화도 가능
                // .requestMatchers("/api/projects/*/documents/**", "/api/documents/**").permitAll()

                // 나머지는 막기
                .anyRequest().authenticated()
            )

            // 폼 로그인 페이지 자체도 잠깐 끄기(리다이렉트 방지)
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
