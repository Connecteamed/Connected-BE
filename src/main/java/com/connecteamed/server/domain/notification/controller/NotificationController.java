package com.connecteamed.server.domain.notification.controller;

import com.connecteamed.server.domain.notification.dto.NotificationListRes;
import com.connecteamed.server.domain.notification.service.NotificationService;
import com.connecteamed.server.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 알림 목록 조회
    @GetMapping
    public ResponseEntity<NotificationListRes> getNotifications() {
        String loginId = SecurityUtil.getCurrentLoginId();
        return ResponseEntity.ok(notificationService.getNotifications(loginId));
    }

    // 알림 삭제
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long notificationId) {
        String loginId = SecurityUtil.getCurrentLoginId();
        notificationService.deleteNotification(notificationId, loginId);
        return ResponseEntity.noContent().build();
    }
}
