package com.connecteamed.server.global.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SecurityUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) {
        SecurityUtil.applicationContext = context;
        log.info("[SecurityUtil] ApplicationContext initialized");
    }

    public static String getCurrentLoginId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.debug("[SecurityUtil] Authentication: {}", authentication);

        // 인증 정보가 있으면 그 정보 사용
        if (authentication != null && authentication.getName() != null) {
            String name = authentication.getName();
            if (name != null && !name.isEmpty() && !name.equals("anonymousUser")) {
                log.info("[SecurityUtil] Using authenticated user: {}", name);
                return name;
            }
        }

        // 개발/테스트 환경에서는 환경변수의 테스트 로그인 ID 반환
        String testLoginId = null;
        if (applicationContext != null) {
            testLoginId = applicationContext.getEnvironment().getProperty("app.test.login-id");
            log.info("[SecurityUtil] app.test.login-id from environment: {}", testLoginId);
        } else {
            log.warn("[SecurityUtil] ApplicationContext is null!");
        }

        if (testLoginId != null && !testLoginId.isEmpty()) {
            log.info("[SecurityUtil] Using test login ID: {}", testLoginId);
            return testLoginId;
        }

        log.info("[SecurityUtil] Using default test login ID: test@example.com");
        return "test@example.com";
    }
}