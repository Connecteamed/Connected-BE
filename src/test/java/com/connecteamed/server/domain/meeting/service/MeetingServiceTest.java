package com.connecteamed.server.domain.meeting.service;

import com.connecteamed.server.domain.meeting.dto.MeetingCreateReq;
import com.connecteamed.server.domain.meeting.dto.MeetingCreateRes;
import com.connecteamed.server.domain.meeting.dto.MeetingUpdateReq;
import com.connecteamed.server.domain.meeting.entity.Meeting;
import com.connecteamed.server.domain.meeting.repository.MeetingAgendaRepository;
import com.connecteamed.server.domain.meeting.repository.MeetingAttendeeRepository;
import com.connecteamed.server.domain.meeting.repository.MeetingRepository;
import com.connecteamed.server.domain.project.entity.Project;
import com.connecteamed.server.domain.project.repository.ProjectRepository;
import com.connecteamed.server.global.apiPayload.code.GeneralErrorCode;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class MeetingServiceTest {

    @Mock private MeetingRepository meetingRepository;
    @Mock private MeetingAgendaRepository meetingAgendaRepository;
    @Mock private MeetingAttendeeRepository meetingAttendeeRepository;
    @Mock private ProjectRepository projectRepository;

    @InjectMocks private MeetingService meetingService;

    @Test
    @DisplayName("회의록 생성: 프로젝트 참조 후 저장하고 응답을 반환한다")
    void createMeeting_success() {
        // given
        Long projectId = 1L;
        var req = new MeetingCreateReq(projectId,"주간 회의", java.time.Instant.parse("2026-01-15T10:00:00Z"), List.of("안건1"), List.of(1L, 2L));
        Project projectRef = mock(Project.class);

        given(projectRepository.findById(projectId)).willReturn(Optional.of(projectRef));
        given(meetingRepository.save(any(Meeting.class))).willAnswer(invocation -> {
            Meeting m = invocation.getArgument(0);
            ReflectionTestUtils.setField(m, "id", 100L);
            ReflectionTestUtils.setField(m, "createdAt", Instant.now());
            return m;
        });

        // when
        MeetingCreateRes res = meetingService.createMeeting(projectId, req);

        // then
        ArgumentCaptor<Meeting> captor = ArgumentCaptor.forClass(Meeting.class);
        then(meetingRepository).should().save(captor.capture());

        Meeting saved = captor.getValue();
        assertThat(saved.getTitle()).isEqualTo("주간 회의");
        assertThat(res.meetingId()).isEqualTo(100L);
        then(meetingAttendeeRepository).should().saveAll(any());
    }

    @Test
    @DisplayName("회의록 수정: 기존 데이터 삭제 후 재등록 로직 검증")
    void updateMeeting_success() {
        // given
        Long meetingId = 100L;
        Project project = mock(Project.class);
        Meeting existingMeeting = Meeting.builder()
                .title("기존 제목")
                .project(project)
                .meetingDate(Instant.now())
                .build();
        ReflectionTestUtils.setField(existingMeeting, "id", meetingId);
        ReflectionTestUtils.setField(existingMeeting, "createdAt", Instant.now());
        ReflectionTestUtils.setField(existingMeeting, "updatedAt", Instant.now());

        List<MeetingUpdateReq.UpdateAgendaInfo> emptyAgendas = List.of();
        var req = new MeetingUpdateReq("수정 제목", Instant.parse("2026-01-15T11:00:00Z"), emptyAgendas, List.of());

        given(meetingRepository.findByIdAndDeletedAtIsNull(meetingId)).willReturn(Optional.of(existingMeeting));

        // when
        meetingService.updateMeeting(meetingId, req);

        // then
        assertThat(existingMeeting.getTitle()).isEqualTo("수정 제목");
        then(meetingAttendeeRepository).should().deleteAllByMeeting(existingMeeting);
    }

    @Test
    @DisplayName("회의록 상세 조회: 존재하지 않는 ID 조회 시 예외 발생")
    void getMeeting_fail_notFound() {
        // given
        Long invalidId = 999L;
        given(meetingRepository.findByIdAndDeletedAtIsNull(invalidId)).willReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> meetingService.getMeeting(invalidId))
                .isInstanceOf(GeneralException.class)
                .hasFieldOrPropertyWithValue("code", GeneralErrorCode.NOT_FOUND);
    }
}