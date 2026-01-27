package com.connecteamed.server.domain.notification.dto;

public record NotificationRes (
        Long id,
        String notificationType,
        String title,
        String content,
        String createdAt,
        boolean isRead,
        String targetUrl
) {}
