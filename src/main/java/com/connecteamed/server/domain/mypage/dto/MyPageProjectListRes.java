package com.connecteamed.server.domain.mypage.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

public class MyPageProjectListRes {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class CompletedProjectList {
        private List<CompletedProjectData> projects;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class CompletedProjectData {
        private Long id;
        private String name;
        private List<String> roles;
        private Instant createdAt;
        private Instant closedAt;
    }
}
