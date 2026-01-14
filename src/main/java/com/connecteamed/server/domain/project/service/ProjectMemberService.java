package com.connecteamed.server.domain.project.service;

import com.connecteamed.server.domain.project.dto.ProjectMemberRes;
import com.connecteamed.server.domain.project.dto.ProjectMemberRoleUpdateReq;

import java.util.List;

public interface ProjectMemberService {
    List<ProjectMemberRes> getProjectMembers(Long projectId);
    ProjectMemberRes updateMemberRoles(Long projectId, Long projectMemberId, ProjectMemberRoleUpdateReq req);
}

