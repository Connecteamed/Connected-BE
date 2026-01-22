package com.connecteamed.server.domain.task.dto;

import com.connecteamed.server.domain.task.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

public record CompletedTaskStatusUpdateReq (
        @NotNull
        TaskStatus status
) {}
