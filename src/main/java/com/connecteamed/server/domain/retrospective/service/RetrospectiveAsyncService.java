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
    public void processAiAnalysis(Long retrospectiveId, String userResult, List<String> taskNames) {
        // AI 분석 호출
        String analyzedResult = geminiProvider.getAnalysis(userResult, taskNames);

        // 결과 반영
        aiRetrospectiveRepository.findById(retrospectiveId).ifPresent(retrospective -> {
            retrospective.update(retrospective.getTitle(), analyzedResult);
        });
    }
}