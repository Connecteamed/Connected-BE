package com.connecteamed.server.domain.meeting.repository;

import com.connecteamed.server.domain.meeting.entity.Meeting;
import com.connecteamed.server.domain.meeting.entity.MeetingAgenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MeetingAgendaRepository extends JpaRepository<MeetingAgenda, Long> {
    List<MeetingAgenda> findAllByMeetingOrderBySortOrderAsc(Meeting meeting);
    void deleteAllByMeeting(Meeting meeting);
}
