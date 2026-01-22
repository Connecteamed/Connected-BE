package com.connecteamed.server.domain.team.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;
import java.util.List;

public class TeamListRes {


    @Schema(description = "팀 목록 데이터")
    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class TeamDataList {

        @Schema(description = "참여 중인 팀(프로젝트) 리스트")
        List<TeamInfo> teams;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class TeamInfo {
        @Schema(description = "팀(프로젝트) ID", example = "1")
        private Long teamId;
        @Schema(description = "팀(프로젝트) 이름", example = "connecteamed")
        private String name;

    }
}
