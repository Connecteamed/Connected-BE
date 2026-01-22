package com.connecteamed.server.domain.task.service;

import com.connecteamed.server.domain.member.entity.Member;
import com.connecteamed.server.domain.member.repository.MemberRepository;
import com.connecteamed.server.domain.task.dto.CompletedTaskDetailRes;
import com.connecteamed.server.domain.task.entity.Task;
import com.connecteamed.server.domain.task.entity.TaskNote;
import com.connecteamed.server.domain.task.enums.TaskStatus;
import com.connecteamed.server.domain.task.repository.TaskAssigneeRepository;
import com.connecteamed.server.domain.task.repository.TaskNoteRepository;
import com.connecteamed.server.domain.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CompletedTaskServiceTest {

    @InjectMocks
    private CompletedTaskService completedTaskService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskAssigneeRepository taskAssigneeRepository;

    @Mock
    private TaskNoteRepository taskNoteRepository;

    @Mock
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {}

    @Test
    @DisplayName("완료 업무 상세 조회 시 내 회고 내용이 포함되어야 한다")
    void getCompletedTaskDetail_Success() {
        // given
        Long taskId = 1L;
        Long memberId = 1L;
        String loginId = "testUser";

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(loginId, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Task task = Task.builder()
                .id(taskId)
                .name("테스트 업무")
                .content("내용")
                .status(TaskStatus.DONE)
                .startDate(java.time.Instant.now())
                .dueDate(java.time.Instant.now())
                .build();

        TaskNote note = TaskNote.builder().content("나의 회고록").build();

        Member mockMember = mock(Member.class);
        given(mockMember.getId()).willReturn(memberId);

        given(memberRepository.findByLoginId(any())).willReturn(Optional.of(mockMember));
        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));
        given(taskNoteRepository.findByTaskIdAndTaskAssignee_ProjectMember_Id(taskId, memberId))
                .willReturn(Optional.of(note));

        // when
        CompletedTaskDetailRes result = completedTaskService.getCompletedTaskDetail(taskId);

        // then
        assertThat(result.noteContent()).isEqualTo("나의 회고록");
        verify(taskNoteRepository, times(1)).findByTaskIdAndTaskAssignee_ProjectMember_Id(taskId, memberId);
    }

    @Test
    @DisplayName("업무 삭제 호출 시 실제로 삭제되지 않고 deletedAt 필드만 채워져야 한다 (Soft Delete)")
    void deleteCompletedTask_SoftDelete() {
        // given
        Long taskId = 1L;
        Task task = spy(Task.builder()
                .id(taskId)
                .status(TaskStatus.DONE)
                .build());

        given(taskRepository.findById(taskId)).willReturn(Optional.of(task));

        // when
        completedTaskService.deleteCompletedTask(taskId);

        // then
        verify(task).softDelete();
        assertThat(task.getDeletedAt()).isNotNull();
    }
}
