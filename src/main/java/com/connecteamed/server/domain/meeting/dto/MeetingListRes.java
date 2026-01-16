package com.connecteamed.server.domain.meeting.dto;

import java.time.Instant;
import java.util.List;

public record MeetingListRes (
    List<MeetingSummary> meetings
) {
    public record MeetingSummary(
            Long meetingId,
            String title,
            Instant meetingDate,
            List<AttendeeSummary> attendees
    ) {}

    public record AttendeeSummary(
            Long attendeeId,
            String name
    ) {}
}