package com.connecteamed.server.domain.notification.repository;

import com.connecteamed.server.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @EntityGraph(attributePaths = {"project"})
    List<Notification> findAllByReceiverLoginIdOrderByCreatedAtDesc(String loginId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.receiver.loginId = :loginId AND n.isRead = false")
    long countUnreadByReceiverLoginId(@Param("loginId") String loginId);
}
