package com.connecteamed.server.domain.meeting.service;

import com.connecteamed.server.domain.meeting.dto.*;
import com.connecteamed.server.domain.meeting.entity.Meeting;
import com.connecteamed.server.domain.meeting.entity.MeetingAgenda;
import com.connecteamed.server.domain.meeting.entity.MeetingAttendee;
import com.connecteamed.server.domain.meeting.repository.MeetingAgendaRepository;
import com.connecteamed.server.domain.meeting.repository.MeetingAttendeeRepository;
import com.connecteamed.server.domain.meeting.repository.MeetingRepository;
import com.connecteamed.server.domain.project.entity.Project;
import com.connecteamed.server.domain.project.repository.ProjectRepository;
import com.connecteamed.server.global.apiPayload.code.GeneralErrorCode;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final MeetingAgendaRepository meetingAgendaRepository;
    private final MeetingAttendeeRepository meetingAttendeeRepository;
    private final ProjectRepository projectRepository;

    // 회의록 생성
    @Transactional
    public MeetingCreateRes createMeeting(Long projectId, MeetingCreateReq request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.NOT_FOUND));
        Meeting meeting = Meeting.builder()
                .project(project)
                .title(request.title())
                .meetingDate(request.meetingDate())
                .build();
        Meeting savedMeeting = meetingRepository.save(meeting);

        // 안건 저장
        if (request.agendas() != null) {
            List<String> agendaTitles = request.agendas();
            List<MeetingAgenda> agendas = java.util.stream.IntStream.range(0, agendaTitles.size())
                    .mapToObj(i -> MeetingAgenda.builder()
                            .meeting(savedMeeting)
                            .title(agendaTitles.get(i))
                            .content("")
                            .sortOrder(i)
                            .build())
                    .toList();
            meetingAgendaRepository.saveAll(agendas);
        }

        if (request.attendeeIds() != null) {
            List<MeetingAttendee> attendees = request.attendeeIds().stream()
                    .map(id -> MeetingAttendee.builder()
                            .meeting(savedMeeting)
                            .attendeeId(id)
                            .build())
                    .toList();
            meetingAttendeeRepository.saveAll(attendees);
        }

        return new MeetingCreateRes(savedMeeting.getId(), savedMeeting.getCreatedAt());

    }
    // 회의록 수정
    @Transactional
    public MeetingDetailRes updateMeeting(Long meetingId, MeetingUpdateReq request) {
        Meeting meeting = meetingRepository.findByIdAndDeletedAtIsNull(meetingId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.NOT_FOUND));

        // 기본 정보 업데이트
        meeting.update(request.title(), request.meetingDate());

        // 안건 업데이트
        List<MeetingAgenda> existingAgendas = meeting.getAgendas();
        List<Long> requestAgendaIds = request.agendas().stream()
                .map(MeetingUpdateReq.UpdateAgendaInfo::id)
                .filter(java.util.Objects::nonNull)
                .toList();

        existingAgendas.removeIf(agenda -> !requestAgendaIds.contains(agenda.getId()));

        request.agendas().forEach(agendaDto -> {
            if (agendaDto.id() != null) {
                existingAgendas.stream()
                        .filter(a -> a.getId().equals(agendaDto.id()))
                        .findFirst()
                        .ifPresent(a -> {
                            a.update(agendaDto.title(), agendaDto.content(), agendaDto.sortOrder());
                        });
            } else {
                meeting.getAgendas().add(MeetingAgenda.builder()
                        .meeting(meeting)
                        .title(agendaDto.title())
                        .content(agendaDto.content())
                        .sortOrder(agendaDto.sortOrder())
                        .build());
            }
        });

        // 참석자 업데이트
        meetingAttendeeRepository.deleteAllByMeeting(meeting);
        List<MeetingAttendee> newAttendees = request.attendeeIds().stream()
                .map(id -> MeetingAttendee.builder()
                        .meeting(meeting)
                        .attendeeId(id)
                        .build())
                .toList();
        meetingAttendeeRepository.saveAll(newAttendees);

        return getMeeting(meetingId);
    }
    // 회의록 상세 조회
    public MeetingDetailRes getMeeting(Long meetingId) {
        Meeting meeting = meetingRepository.findByIdAndDeletedAtIsNull(meetingId)
                .orElseThrow(() -> new GeneralException(GeneralErrorCode.NOT_FOUND));

        return new MeetingDetailRes(
                meeting.getId(),
                meeting.getProject().getId(),
                meeting.getTitle(),
                meeting.getMeetingDate(),
                meeting.getCreatedAt(),
                meeting.getUpdatedAt(),
                meeting.getAgendas().stream().map(a -> new MeetingDetailRes.AgendaInfo(
                        a.getId(), a.getTitle(), a.getContent(), a.getSortOrder(),
                        a.getCreatedAt(), a.getUpdatedAt()
                )).toList(),
                meeting.getAttendees().stream().map(at -> new MeetingDetailRes.AttendeeInfo(
                        at.getId(), at.getAttendeeId(), "참석자 " + at.getAttendeeId()
                )).toList()
        );
    }
    // 4. 회의록 목록 조회
    public MeetingListRes getMeetings(Long projectId) {
        List<Meeting> meetings = meetingRepository.findAllByProjectIdAndDeletedAtIsNull(projectId);

        return new MeetingListRes(
                meetings.stream().map(m -> new MeetingListRes.MeetingSummary(
                        m.getId(),
                        m.getTitle(),
                        m.getMeetingDate(),
                        m.getAttendees().stream().map(at -> new MeetingListRes.AttendeeSummary(
                                at.getAttendeeId(), "참석자"
                        )).toList()
                )).toList()
        );
    }
}
