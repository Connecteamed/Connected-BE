package com.connecteamed.server.domain.project.controller;

import com.connecteamed.server.domain.project.dto.ProjectMemberRes;
import com.connecteamed.server.domain.project.dto.ProjectMemberRoleUpdateReq;
import com.connecteamed.server.domain.project.service.ProjectMemberService;
import com.connecteamed.server.global.apiPayload.ApiResponse;
import com.connecteamed.server.global.apiPayload.code.GeneralSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    // 프로젝트별 팀원 목록 & 역할
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectMemberRes>>> getMembers(@PathVariable Long projectId) {
        List<ProjectMemberRes> res = projectMemberService.getProjectMembers(projectId);
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode._OK, res));
    }

    // 팀원 역할 할당(교체)
    @PatchMapping("/{projectMemberId}/roles")
    public ResponseEntity<ApiResponse<ProjectMemberRes>> updateRoles(
            @PathVariable Long projectId,
            @PathVariable Long projectMemberId,
            @Valid @RequestBody ProjectMemberRoleUpdateReq req
    ) {
        ProjectMemberRes res = projectMemberService.updateMemberRoles(projectId, projectMemberId, req);
        return ResponseEntity.ok(ApiResponse.onSuccess(GeneralSuccessCode._OK, res));
    }
}
