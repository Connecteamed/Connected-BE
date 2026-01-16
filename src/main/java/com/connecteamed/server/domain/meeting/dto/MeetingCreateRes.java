package com.connecteamed.server.domain.meeting.dto;

import java.time.Instant;

public record MeetingCreateRes(
    Long meetingId,
    Instant createdAt
) {}
