package com.connecteamed.server.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProjectUpdateReq {

    @JsonProperty("name")
    @Schema(description = "프로젝트명", example = "UMC 7기", required = true)
    private String name;

    @JsonProperty("goal")
    @Schema(description = "프로젝트 목표", example = "앱 런칭", required = true)
    private String goal;

    @JsonProperty("requiredRoleNames")
    @Schema(description = "필요 역할 목록", example = "[\"DESIGNER\", \"SERVER\", \"ANDROID\"]", required = true)
    private List<String> requiredRoleNames;
}

