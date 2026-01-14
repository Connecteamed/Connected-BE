package com.connecteamed.server.domain.meeting.dto;

import java.util.List;

public record MeetingDetailRes(
    Long meetingId,
    Long projectId,
    String title,
    String meetingDate,
    String createdAt,
    String updatedAt,
    List<AgendaInfo> agendas,
    List<AttendeeInfo> attendees
) {
    public record AgendaInfo(
            Long id,
            String title,
            String content,
            Integer sortOrder,
            String createdAt,
            String updatedAt
    ) {}

    public record AttendeeInfo(
            Long id,
            Long attendeeId
    ) {}
}
