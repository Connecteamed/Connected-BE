package com.connecteamed.server.domain.retrospective.service;

import com.connecteamed.server.domain.retrospective.repository.AiRetrospectiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RetrospectiveAsyncService {
    private final GeminiProvider geminiProvider;
    private final AiRetrospectiveRepository aiRetrospectiveRepository;

    @Async("AsyncExecutor")
    @Transactional
    public void processAiAnalysis(
            Long retrospectiveId,
            String projectName,
            String projectGoal,
            String retrospectiveTitle,
            String totalResult,
            String role,
            String myTaskList,
            String otherTasks
    ) {
        // AI 분석 호출
        String analyzedResult = geminiProvider.getAnalysis(
                projectName, projectGoal, retrospectiveTitle, totalResult, role, myTaskList, otherTasks
        );

        // 결과 반영
        aiRetrospectiveRepository.findById(retrospectiveId).ifPresent(retrospective -> {
            retrospective.update(retrospective.getTitle(), analyzedResult);
        });
    }
}