package com.connecteamed.server.domain.meeting.dto;

import java.util.List;

public record MeetingUpdateReq (
        String title,
        String meetingDate,
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
