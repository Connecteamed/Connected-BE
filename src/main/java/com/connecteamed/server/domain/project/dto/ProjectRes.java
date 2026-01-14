package com.connecteamed.server.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;

public class ProjectRes {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class CreateResponse {
        @JsonProperty("projectId")
        @Schema(description = "생성된 프로젝트 ID", example = "105")
        private Long projectId;

        @JsonProperty("createdAt")
        @Schema(description = "생성 시간 (UTC)", example = "2026-01-15T00:32:50.021Z")
        private Instant createdAt;
    }
}
