package com.connecteamed.server.domain.task.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record TaskAssigneeUpdateReq(
        @NotNull List<Long> assigneeProjectMemberIds
) {
}
