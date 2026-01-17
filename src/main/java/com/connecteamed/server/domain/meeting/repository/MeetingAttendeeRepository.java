package com.connecteamed.server.domain.meeting.repository;

import com.connecteamed.server.domain.meeting.entity.Meeting;
import com.connecteamed.server.domain.meeting.entity.MeetingAttendee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingAttendeeRepository extends JpaRepository<MeetingAttendee, Long> {
    void deleteAllByMeeting(Meeting meeting);
}
