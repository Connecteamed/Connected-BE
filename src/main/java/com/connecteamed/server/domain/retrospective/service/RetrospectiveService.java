package com.connecteamed.server.domain.retrospective.service;

import com.connecteamed.server.domain.project.entity.Project;
import com.connecteamed.server.domain.project.entity.ProjectMember;
import com.connecteamed.server.domain.project.repository.ProjectMemberRepository;
import com.connecteamed.server.domain.project.repository.ProjectRepository;
import com.connecteamed.server.domain.retrospective.dto.RetrospectiveCreateReq;
import com.connecteamed.server.domain.retrospective.dto.RetrospectiveCreateRes;
import com.connecteamed.server.domain.retrospective.dto.RetrospectiveDetailRes;
import com.connecteamed.server.domain.retrospective.entity.AiRetrospective;
import com.connecteamed.server.domain.retrospective.repository.AiRetrospectiveRepository;
import com.connecteamed.server.domain.retrospective.repository.RetrospectiveRepository;
import com.connecteamed.server.domain.task.entity.Task;
import com.connecteamed.server.domain.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrospectiveService {

    private final AiRetrospectiveRepository aiRetrospectiveRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public RetrospectiveCreateRes createAiRetrospective(Long projectId, Long memberId, RetrospectiveCreateReq request){
        // 데이터 조회 (프로젝트, 작성자, 업무 리스트)
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));

        ProjectMember writer = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("팀원 정보를 찾을 수 없습니다."));

        List<Task> selectedTasks = taskRepository.findAllById(request.taskIds());

        // AI 분석 수행
        String aiAnalyzedResult = simulateGeminiAnalysis(request.projectResult(), selectedTasks);

        // 3. AiRetrospective 엔티티 생성
        AiRetrospective retrospective = AiRetrospective.builder()
                .project(project)
                .writer(writer)
                .title(request.title())
                .projectResult(aiAnalyzedResult)
                .build();

        // 4. 연관된 업무들 매핑 (RetrospectiveTask 생성)
        selectedTasks.forEach(retrospective::addRetrospectiveTask);

        // 5. 저장
        AiRetrospective saved = aiRetrospectiveRepository.save(retrospective);

        return new RetrospectiveCreateRes(saved.getPublicId(), saved.getTitle());
    }
    @Transactional(readOnly = true)
    public RetrospectiveDetailRes getRetrospectiveDetail(UUID retrospectiveId) {
        AiRetrospective retrospective = aiRetrospectiveRepository.findByPublicId(retrospectiveId)
                .orElseThrow(() -> new RuntimeException("회고를 찾을 수 없습니다."));

        return new RetrospectiveDetailRes(
                retrospective.getPublicId(),
                retrospective.getTitle(),
                retrospective.getProjectResult(),
                retrospective.getCreatedAt(),
                retrospective.getWriter().getId()
        );
    }

    // Gemini 연동 전 임시 메서드
    private String simulateGeminiAnalysis(String userPerformance, List<Task> tasks) {
        StringBuilder sb = new StringBuilder();
        sb.append("[AI 분석 결과]\n");
        sb.append("사용자 입력 성과: ").append(userPerformance).append("\n\n");
        sb.append("분석된 업무 리스트:\n");
        tasks.forEach(t -> sb.append("- ").append(t.getName()).append("\n"));
        sb.append("\n이 업무들을 바탕으로 Gemini가 곧 멋진 회고를 생성할 예정입니다!");
        return sb.toString();
    }
}
