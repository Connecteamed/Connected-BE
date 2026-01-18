package com.connecteamed.server.domain.meeting.dto;

import java.time.Instant;
import java.util.List;

public record MeetingUpdateReq (
        String title,
        Instant meetingDate,
        List<UpdateAgendaInfo> agendas,
        List<Long> attendeeIds
) {
    public record UpdateAgendaInfo (
            Long id,
            String title,
            String content,
            Integer sortOrder
    ) {}
}
