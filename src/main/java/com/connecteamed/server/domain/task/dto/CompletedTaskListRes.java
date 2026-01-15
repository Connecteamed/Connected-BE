package com.connecteamed.server.domain.task.dto;

import java.util.List;

public record CompletedTaskListRes(
        List<TaskSummary> tasks
) {
    public record TaskSummary(
            Long taskId,
            String name,
            String content,
            String startDate,
            String dueDate,
            String status,
            List<String> assigneeNames
    ) {}
}
