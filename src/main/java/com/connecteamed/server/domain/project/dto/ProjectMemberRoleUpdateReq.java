package com.connecteamed.server.domain.project.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ProjectMemberRoleUpdateReq(
        @NotNull(message = "roleIds는 null일 수 없습니다.")
        List<Long> roleIds
) {}
