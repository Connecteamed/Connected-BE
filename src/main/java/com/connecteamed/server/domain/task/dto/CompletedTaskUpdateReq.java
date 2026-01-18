package com.connecteamed.server.domain.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CompletedTaskUpdateReq (
    @NotBlank(message = "업무 이름은 비워둘 수 없습니다.")
    String name,
    @NotNull(message = "업무 내용은 null일 수 없습니다.")
    String content,
    @NotNull(message = "회고 내용은 null일 수 없습니다.")
    String noteContent
) {}
