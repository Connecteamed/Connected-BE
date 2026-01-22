package com.connecteamed.server.domain.notification.repository;

import com.connecteamed.server.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n " +
            "JOIN FETCH n.project " +
            "WHERE n.receiver.recordId = :userId " +
            "ORDER BY n.createdAt DESC")
    List<Notification> findAllByReceiverRecordId(@Param("userId") String userId);
}
