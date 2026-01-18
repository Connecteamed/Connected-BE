package com.connecteamed.server.domain.project.dto;

import java.util.List;

public record ProjectMemberRes(
        Long projectMemberId,
        Long memberId,
        String memberName,
        List<RoleRes> roles
) {
    public record RoleRes(
            Long roleId,
            String roleName
    ) {}
}
