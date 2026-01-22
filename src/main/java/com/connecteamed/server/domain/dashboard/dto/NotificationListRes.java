package com.connecteamed.server.domain.dashboard.dto;

import java.time.Instant;
import java.util.List;

public record NotificationListRes (
        List<NotificationRes> notifications
) {
    public record NotificationRes (
            Long id,
            String message,
            String teamName,
            boolean isRead,
            Instant createdAt
    ) {}
}
