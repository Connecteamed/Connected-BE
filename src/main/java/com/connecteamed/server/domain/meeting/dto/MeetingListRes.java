package com.connecteamed.server.domain.meeting.dto;

import java.util.List;

public record MeetingListRes (
    List<MeetingSummary> meetings
) {
    public record MeetingSummary(
            String meetingId,
            String title,
            String meetingDate,
            List<AttendeeSummary> attendees
    ) {}

    public record AttendeeSummary(
            Long attendeeId
    ) {}
}