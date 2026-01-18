package com.connecteamed.server.domain.project.dto;

import java.util.List;

public record ProjectRoleListRes(
        List<RoleItem> roles
) {
    public record RoleItem(Long roleId, String name) {}
}
