package com.connecteamed.server.domain.task.dto;

public record CompletedTaskUpdateReq (
    String name,
    String content,
    String noteContent
) {}
