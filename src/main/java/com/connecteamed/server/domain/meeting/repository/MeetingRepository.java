package com.connecteamed.server.domain.meeting.repository;

import com.connecteamed.server.domain.meeting.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findAllByProjectIdAndDeletedAtIsNull(Long projectId);
    Optional<Meeting> findByIdAndDeletedAtIsNull(Long meetingId);
}
