package com.connecteamed.server.domain.meeting.dto;

import java.time.Instant;
import java.util.List;

public record MeetingCreateReq (
    Long projectId,
    String title,
    Instant meetingDate,
    List<String> agendas,
    List<Long> attendeeIds
) {}
