package com.connecteamed.server.domain.meeting.repository;

import com.connecteamed.server.domain.meeting.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findAllByProjectIdAndDeletedAtIsNull(Long projectId);
    Optional<Meeting> findByIdAndDeletedAtIsNull(Long meetingId);

    @Query("SELECT m FROM Meeting m " +
            "JOIN FETCH m.project " +
            "WHERE m.receiver.recordId = :userId " + // 수신자 필드명 확인 필요
            "AND m.startTime >= :startOfDay AND m.startTime < :endOfDay " +
            "AND m.deletedAt IS NULL")
    List<Meeting> findAllByMemberAndDate(
            @Param("userId") String userId,
            @Param("startOfDay") Instant startOfDay,
            @Param("endOfDay") Instant endOfDay
    );
}
