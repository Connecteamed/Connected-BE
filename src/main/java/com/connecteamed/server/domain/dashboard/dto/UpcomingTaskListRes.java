package com.connecteamed.server.domain.dashboard.dto;

import java.util.List;

public record UpcomingTaskListRes (
        List<UpcomingTaskRes> tasks
) {
    public record UpcomingTaskRes (
            Long id,
            String status,
            String title,
            String teamName,
            String dueDate
    ) {}
}
