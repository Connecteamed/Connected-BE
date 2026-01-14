package com.connecteamed.server.domain.meeting.dto;

import java.util.List;

public record MeetingCreateReq (
    Long projectId,
    String title,
    String meetingDate,
    List<String> agendas,
    List<Long> attendeeIds
) {}
