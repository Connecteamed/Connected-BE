package com.connecteamed.server.domain.dashboard.service;

import com.connecteamed.server.domain.dashboard.dto.DashboardRes;
import com.connecteamed.server.domain.retrospective.entity.AiRetrospective;
import com.connecteamed.server.domain.retrospective.repository.RetrospectiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final RetrospectiveRepository retrospectiveRepository;

    /**
     * 최근 회고 목록 조회
     * @return 회고 목록 응답 DTO
     */
    public DashboardRes.RetrospectiveListRes getRecentRetrospectives() {
        List<AiRetrospective> retrospectives = retrospectiveRepository.findRecentRetrospectives();

        List<DashboardRes.RetrospectiveInfo> retrospectiveInfos = retrospectives.stream()
                .map(retrospective -> DashboardRes.RetrospectiveInfo.builder()
                        .id(retrospective.getId())
                        .title(retrospective.getTitle())
                        .teamName(retrospective.getProject().getName())
                        .writtenDate(retrospective.getCreatedAt().toLocalDate())
                        .build())
                .collect(Collectors.toList());

        return DashboardRes.RetrospectiveListRes.builder()
                .retrospectives(retrospectiveInfos)
                .build();
    }
}
