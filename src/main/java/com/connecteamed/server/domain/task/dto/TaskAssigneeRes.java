package com.connecteamed.server.domain.task.dto;

import java.util.UUID;

public record TaskAssigneeRes(
        Long projectMemberId,
        Long memberId,
        UUID memberPublicId,
        String memberName
) {
}
