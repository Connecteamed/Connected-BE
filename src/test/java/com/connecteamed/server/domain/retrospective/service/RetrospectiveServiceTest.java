package com.connecteamed.server.domain.retrospective.service;

import com.connecteamed.server.domain.project.entity.Project;
import com.connecteamed.server.domain.project.entity.ProjectMember;
import com.connecteamed.server.domain.project.repository.ProjectMemberRepository;
import com.connecteamed.server.domain.project.repository.ProjectRepository;
import com.connecteamed.server.domain.retrospective.dto.RetrospectiveCreateReq;
import com.connecteamed.server.domain.retrospective.dto.RetrospectiveCreateRes;
import com.connecteamed.server.domain.retrospective.entity.AiRetrospective;
import com.connecteamed.server.domain.retrospective.repository.AiRetrospectiveRepository;
import com.connecteamed.server.domain.task.entity.Task;
import com.connecteamed.server.domain.task.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class RetrospectiveServiceTest {
    @Mock
    private AiRetrospectiveRepository aiRetrospectiveRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMemberRepository projectMemberRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private GeminiProvider geminiProvider;

    @InjectMocks
    private RetrospectiveService retrospectiveService;

    @Test
    @DisplayName("AI 회고 생성 서비스 로직 검증")
    void createAiRetrospectiveSuccess() {
        // given
        Long projectId = 1L;
        Long memberId = 1L;
        RetrospectiveCreateReq request = new RetrospectiveCreateReq("테스트 제목", "내 성과", List.of(1L, 2L));

        Project project = mock(Project.class);
        ProjectMember writer = mock(ProjectMember.class);
        List<Task> tasks = List.of(mock(Task.class), mock(Task.class));

        AiRetrospective savedRetrospective = AiRetrospective.builder()
                .title("테스트 제목")
                .build();
        AiRetrospective spyRetrospective = spy(savedRetrospective);
        UUID mockUuid = UUID.randomUUID();
        given(spyRetrospective.getPublicId()).willReturn(mockUuid);

        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
        given(projectMemberRepository.findById(memberId)).willReturn(Optional.of(writer));
        given(taskRepository.findAllById(any())).willReturn(tasks);
        given(geminiProvider.getAnalysis(any(), any())).willReturn("AI가 분석한 아주 멋진 결과");
        given(aiRetrospectiveRepository.save(any())).willReturn(spyRetrospective);

        // when
        RetrospectiveCreateRes result = retrospectiveService.createAiRetrospective(projectId, memberId, request);

        // then
        assertNotNull(result);
        assertEquals(mockUuid, result.retrospectiveId());
        verify(aiRetrospectiveRepository, times(1)).save(any()); // 저장이 한 번 일어났는지 확인
        verify(geminiProvider, times(1)).getAnalysis(any(), any()); // AI 분석을 호출했는지 확인
    }

    @Test
    @DisplayName("회고 수정 로직 검증")
    void updateRetrospectiveTest() {
        // given
        UUID publicId = UUID.randomUUID();
        AiRetrospective retrospective = mock(AiRetrospective.class);
        given(aiRetrospectiveRepository.findByPublicId(publicId)).willReturn(Optional.of(retrospective));

        // when
        retrospectiveService.updateRetrospective(publicId, new com.connecteamed.server.domain.retrospective.dto.RetrospectiveUpdateReq("새 제목", "새 결과"));

        // then
        verify(retrospective).update(anyString(), anyString()); // 엔티티의 update 메서드가 호출되었는지 확인
    }

    @Test
    @DisplayName("회고 삭제 로직 검증")
    void deleteRetrospectiveTest() {
        // given
        UUID publicId = UUID.randomUUID();
        AiRetrospective retrospective = mock(AiRetrospective.class);
        given(aiRetrospectiveRepository.findByPublicId(publicId)).willReturn(Optional.of(retrospective));

        // when
        retrospectiveService.deleteRetrospective(publicId);

        // then
        verify(aiRetrospectiveRepository, times(1)).delete(retrospective);
    }
}
