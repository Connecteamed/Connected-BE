package com.connecteamed.server.domain.task.dto;

import java.time.Instant;
import java.util.List;

public record CompletedTaskListRes(
        List<TaskSummary> tasks
) {
    public record TaskSummary(
            Long taskId,
            String name,
            String content,
            Instant startDate,
            Instant dueDate,
            String status,
            List<String> assigneeNames
    ) {}
}
