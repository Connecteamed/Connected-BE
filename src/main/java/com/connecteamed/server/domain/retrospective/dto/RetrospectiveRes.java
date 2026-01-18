package com.connecteamed.server.domain.retrospective.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

public class RetrospectiveRes {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class RetrospectiveList {
        private List<RetrospectiveInfo> retrospectives;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class RetrospectiveInfo {
        private Long id;
        private String title;
        private Instant createdAt;
    }

}
