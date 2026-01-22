package com.connecteamed.server.domain.task.dto;

import com.connecteamed.server.domain.task.enums.TaskStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TaskDetailRes(
        UUID taskId,
        Long projectId,
        String name,
        String content,
        TaskStatus status,
        Instant startDate,
        Instant dueDate,
        List<TaskAssigneeRes> assignees,
        Instant createdAt,
        Instant updatedAt
) {
}
