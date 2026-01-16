package com.connecteamed.server.domain.meeting.dto;

import java.time.Instant;
import java.util.List;

public record MeetingDetailRes(
    Long meetingId,
    Long projectId,
    String title,
    Instant meetingDate,
    Instant createdAt,
    Instant updatedAt,
    List<AgendaInfo> agendas,
    List<AttendeeInfo> attendees
) {
    public record AgendaInfo(
            Long id,
            String title,
            String content,
            Integer sortOrder,
            Instant createdAt,
            Instant updatedAt
    ) {}

    public record AttendeeInfo(
            Long id,
            Long attendeeId,
            String name
    ) {}
}
