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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
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
                .orElseThrow(() -> new IllegalArgumentException("프로젝트를 찾을 수 없습니다."));
        Meeting meeting = Meeting.builder()
                .project(project)
                .title(request.title())
                .meetingDate(OffsetDateTime.parse(request.meetingDate()))
                .build();
        Meeting savedMeeting = meetingRepository.save(meeting);

        // 안건 저장
        if (request.agendas() != null) {
            List<MeetingAgenda> agendas = request.agendas().stream()
                    .map(title -> MeetingAgenda.builder()
                            .meeting(savedMeeting)
                            .title(title)
                            .content("")
                            .sortOrder(0)
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

        return new MeetingCreateRes(savedMeeting.getId(), savedMeeting.getCreatedAt().toString());

    }
    // 회의록 수정
    @Transactional
    public MeetingDetailRes updateMeeting(Long meetingId, MeetingUpdateReq request) {
        Meeting meeting = meetingRepository.findByIdAndDeletedAtIsNull(meetingId)
                .orElseThrow(() -> new IllegalArgumentException("회의록을 찾을 수 없습니다."));

        // 기본 정보 업데이트
        meeting.update(request.title(), request.meetingDate());

        // 안건 업데이트
        meetingAgendaRepository.deleteAllByMeeting(meeting);
        List<MeetingAgenda> newAgendas = request.agendas().stream()
                .map(a -> MeetingAgenda.builder()
                        .meeting(meeting)
                        .title(a.title())
                        .content(a.content())
                        .sortOrder(a.sortOrder())
                        .build())
                .toList();
        meetingAgendaRepository.saveAll(newAgendas);

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
                .orElseThrow(() -> new IllegalArgumentException("회의록을 찾을 수 없습니다."));

        return new MeetingDetailRes(
                meeting.getId(),
                meeting.getProject().getId(),
                meeting.getTitle(),
                meeting.getMeetingDate().toString(),
                meeting.getCreatedAt().toString(),
                meeting.getUpdatedAt().toString(),
                meeting.getAgendas().stream().map(a -> new MeetingDetailRes.AgendaInfo(
                        a.getId(), a.getTitle(), a.getContent(), a.getSortOrder(),
                        a.getCreatedAt().toString(), a.getUpdatedAt().toString()
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
                        m.getMeetingDate().toString(),
                        m.getAttendees().stream().map(at -> new MeetingListRes.AttendeeSummary(
                                at.getAttendeeId(), "참석자"
                        )).toList()
                )).toList()
        );
    }
}
