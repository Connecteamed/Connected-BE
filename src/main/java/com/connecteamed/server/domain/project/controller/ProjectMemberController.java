package com.connecteamed.server.domain.project.controller;

import com.connecteamed.server.domain.project.dto.ProjectMemberRes;
import com.connecteamed.server.domain.project.dto.ProjectMemberRoleUpdateReq;
import com.connecteamed.server.domain.project.dto.ProjectRoleListRes;
import com.connecteamed.server.domain.project.service.ProjectMemberService;
import com.connecteamed.server.domain.project.service.ProjectService;
import com.connecteamed.server.global.apiPayload.ApiResponse;
import com.connecteamed.server.global.apiPayload.code.GeneralSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    // 프로젝트별 팀원 목록 & 역할
    @GetMapping("/members")
    public ResponseEntity<ApiResponse<List<ProjectMemberRes>>> getMembers(@PathVariable Long projectId) {
        List<ProjectMemberRes> res = projectMemberService.getProjectMembers(projectId);
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode._OK, res));
    }

    // 팀원 역할 할당(교체)
    @PatchMapping("/members/{projectMemberId}/roles")
    public ResponseEntity<ApiResponse<ProjectMemberRes>> updateRoles(
            @PathVariable Long projectId,
            @PathVariable Long projectMemberId,
            @Valid @RequestBody ProjectMemberRoleUpdateReq req
    ) {
        ProjectMemberRes res = projectMemberService.updateMemberRoles(projectId, projectMemberId, req);
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode._OK, res));
    }

    // 프로젝트 역할 목록 조회 (project_required_role 기반)
    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<ProjectRoleListRes>> getProjectRoles(@PathVariable Long projectId) {
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode._OK, projectMemberService.getProjectRoles(projectId)));
    }
}
