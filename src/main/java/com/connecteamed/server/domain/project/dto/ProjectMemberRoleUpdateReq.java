package com.connecteamed.server.domain.project.dto;

import java.util.List;

public record ProjectMemberRoleUpdateReq(
        List<Long> roleIds
) {}
