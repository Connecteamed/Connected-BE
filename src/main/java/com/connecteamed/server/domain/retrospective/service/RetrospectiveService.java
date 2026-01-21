package com.connecteamed.server.domain.retrospective.service;

import com.connecteamed.server.domain.project.entity.Project;
import com.connecteamed.server.domain.project.entity.ProjectMember;
import com.connecteamed.server.domain.project.repository.ProjectMemberRepository;
import com.connecteamed.server.domain.project.repository.ProjectRepository;
import com.connecteamed.server.domain.retrospective.dto.*;
import com.connecteamed.server.domain.retrospective.entity.AiRetrospective;
import com.connecteamed.server.domain.retrospective.repository.AiRetrospectiveRepository;
import com.connecteamed.server.domain.retrospective.repository.RetrospectiveRepository;
import com.connecteamed.server.domain.task.entity.Task;
import com.connecteamed.server.domain.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetrospectiveService {

    private final AiRetrospectiveRepository aiRetrospectiveRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final GeminiProvider geminiProvider;

    // ai 회고 생성
    @Transactional
    public RetrospectiveCreateRes createAiRetrospective(Long projectId, Long memberId, RetrospectiveCreateReq request){
        // 데이터 조회 (프로젝트, 작성자, 업무 리스트)
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));

        ProjectMember writer = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("팀원 정보를 찾을 수 없습니다."));

        List<Task> selectedTasks = taskRepository.findAllById(request.taskIds());

        // AI 분석 수행
        List<String> taskNames = selectedTasks.stream().map(Task::getName).toList();
        String aiAnalyzedResult = geminiProvider.getAnalysis(request.projectResult(), taskNames);

        AiRetrospective retrospective = AiRetrospective.builder()
                .project(project)
                .writer(writer)
                .title(request.title())
                .projectResult(aiAnalyzedResult)
                .build();

        selectedTasks.forEach(retrospective::addRetrospectiveTask);
        AiRetrospective saved = aiRetrospectiveRepository.save(retrospective);

        return new RetrospectiveCreateRes(saved.getId(), saved.getTitle());
    }

    // ai 회고 상세 조회
    public RetrospectiveDetailRes getRetrospectiveDetail(Long projectId, Long retrospectiveId) {
        AiRetrospective retrospective = aiRetrospectiveRepository.findByIdAndProjectId(retrospectiveId, projectId)
                .orElseThrow(() -> new RuntimeException("회고를 찾을 수 없습니다."));

        return new RetrospectiveDetailRes(
                retrospective.getId(),
                retrospective.getTitle(),
                retrospective.getProjectResult(),
                retrospective.getCreatedAt(),
                retrospective.getWriter().getId()
        );
    }

    // ai 회고 목록 조회
    public RetrospectiveListRes getRetrospectivesByProject(Long projectId) {
        // 프로젝트 ID로 회고 목록 조회
        List<AiRetrospective> retrospectives = aiRetrospectiveRepository.findAllByProjectIdOrderByCreatedAtDesc(projectId);

        List<RetrospectiveListRes.RetrospectiveSummary> summaries = retrospectives.stream()
                .map(r -> new RetrospectiveListRes.RetrospectiveSummary(
                        r.getId(),
                        r.getTitle(),
                        r.getCreatedAt()))
                .collect(Collectors.toList());

        return new RetrospectiveListRes(summaries);
    }

    // 회고 수정
    @Transactional
    public void updateRetrospective(Long memberId, Long projectId, Long retrospectiveId, RetrospectiveUpdateReq request) {
        AiRetrospective retrospective = aiRetrospectiveRepository.findByIdAndProjectId(retrospectiveId, projectId)
                .orElseThrow(() -> new RuntimeException("회고를 찾을 수 없습니다."));

        if (!retrospective.getWriter().getMember().getId().equals(memberId)) {
            throw new RuntimeException("해당 회고를 수정할 권한이 없습니다.");
        }
        retrospective.update(request.title(), request.projectResult());
    }

    // 회고 삭제
    @Transactional
    public void deleteRetrospective(Long memberId, Long projectId, Long retrospectiveId) {
        AiRetrospective retrospective = aiRetrospectiveRepository.findByIdAndProjectId(retrospectiveId, projectId)
                .orElseThrow(() -> new RuntimeException("삭제하려는 회고를 찾을 수 없습니다."));

        if (!retrospective.getWriter().getMember().getId().equals(memberId)) {
            throw new RuntimeException("해당 회고를 삭제할 권한이 없습니다.");
        }

        aiRetrospectiveRepository.delete(retrospective);
    }
}
