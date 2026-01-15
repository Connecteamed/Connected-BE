package com.connecteamed.server.domain.task.dto;

import com.connecteamed.server.domain.task.enums.TaskStatus;

public record CompletedTaskStatusUpdateReq (
        TaskStatus status
) {}
