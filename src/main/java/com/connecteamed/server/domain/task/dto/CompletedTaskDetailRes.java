package com.connecteamed.server.domain.task.dto;

import java.util.List;

public record CompletedTaskDetailRes (
        Long taskId,
        String name,
        String content,
        String startDate,
        String dueDate,
        String status,
        List<String> assigneeNames,
        String noteContent
) {}
