package com.connecteamed.server.domain.project.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProjectUpdateReq {

    @JsonProperty("name")
    @Schema(description = "프로젝트명", example = "UMC 7기")
    @NotBlank(message = "프로젝트명은 필수 입력 값입니다.")
    private String name;

    @JsonProperty("goal")
    @Schema(description = "프로젝트 목표", example = "앱 런칭")
    @NotBlank(message = "프로젝트 목표는 필수 입력 값입니다.")
    private String goal;

    @JsonProperty("requiredRoleNames")
    @Schema(description = "필요 역할 목록", example = "[\"DESIGNER\", \"SERVER\", \"ANDROID\"]")
    @NotEmpty(message = "필요 역할은 필수 입력 값입니다.")
    private List<String> requiredRoleNames;
}
