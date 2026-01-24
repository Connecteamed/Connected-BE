package com.connecteamed.server.domain.task.dto;

public record TaskAssigneeRes(
        Long projectMemberId,
        Long memberId,
        String memberName
) {
}
