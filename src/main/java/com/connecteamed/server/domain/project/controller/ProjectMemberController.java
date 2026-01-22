package com.connecteamed.server.domain.project.controller;

import com.connecteamed.server.domain.project.dto.ProjectMemberRes;
import com.connecteamed.server.domain.project.dto.ProjectMemberRoleUpdateReq;
import com.connecteamed.server.domain.project.dto.ProjectRoleListRes;
import com.connecteamed.server.domain.project.service.ProjectMemberService;
import com.connecteamed.server.global.apiPayload.ApiResponse;
import com.connecteamed.server.global.apiPayload.code.GeneralSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}")
@Tag(name = "Project-Member", description = "팀원 & 역할 관련 API")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    @Operation(summary = "프로젝트별 팀원 목록&역할", description = "프로젝트별 팀원 목록&역할 조회 API입니다.")
    @GetMapping("/members")
    public ResponseEntity<ApiResponse<List<ProjectMemberRes>>> getMembers(@PathVariable Long projectId) {
        List<ProjectMemberRes> res = projectMemberService.getProjectMembers(projectId);
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode._OK, res));
    }

    @Operation(summary = "팀원 역할 할당(교체)", description = "팀원 역할 할당(교체) API입니다.")
    @PatchMapping("/members/{projectMemberId}/roles")
    public ResponseEntity<ApiResponse<ProjectMemberRes>> updateRoles(
            @PathVariable Long projectId,
            @PathVariable Long projectMemberId,
            @Valid @RequestBody ProjectMemberRoleUpdateReq req
    ) {
        ProjectMemberRes res = projectMemberService.updateMemberRoles(projectId, projectMemberId, req);
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode._OK, res));
    }

    @Operation(summary = "프로젝트 역할 목록 조회", description = "프로젝트 역할 목록 조회 API입니다.")
    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<ProjectRoleListRes>> getProjectRoles(@PathVariable Long projectId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode._OK, projectMemberService.getProjectRoles(projectId)));
    }
}
