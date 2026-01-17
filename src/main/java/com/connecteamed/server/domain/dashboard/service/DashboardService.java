package com.connecteamed.server.domain.dashboard.service;

import com.connecteamed.server.domain.dashboard.dto.DashboardRes;
import com.connecteamed.server.domain.retrospective.entity.AiRetrospective;
import com.connecteamed.server.domain.retrospective.repository.RetrospectiveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final RetrospectiveRepository retrospectiveRepository;

    /**
     * 최근 회고 목록 조회
     * @return 회고 목록 응답 DTO (모든 회고)
     */
    public DashboardRes.RetrospectiveListRes getRecentRetrospectives() {
        List<AiRetrospective> retrospectives = retrospectiveRepository.findRecentRetrospectives();
        return convertToResponse(retrospectives);
    }

    /**
     * 로그인 사용자가 작성한 최근 회고 목록 조회
     * @param username 로그인한 사용자의 로그인 아이디
     * @return 사용자가 작성한 회고 목록 응답 DTO
     */
    public DashboardRes.RetrospectiveListRes getRecentRetrospectives(String username) {
        List<AiRetrospective> retrospectives = retrospectiveRepository.findRecentRetrospectivesByUsername(username);
        return convertToResponse(retrospectives);
    }

    /**
     * AiRetrospective 리스트를 Response DTO로 변환
     * @param retrospectives DB에서 조회한 회고 엔티티 리스트
     * @return 변환된 회고 목록 응답 DTO
     */
    private DashboardRes.RetrospectiveListRes convertToResponse(List<AiRetrospective> retrospectives) {
        List<DashboardRes.RetrospectiveInfo> retrospectiveInfos = retrospectives.stream()
                .map(retrospective -> DashboardRes.RetrospectiveInfo.builder()
                        .id(retrospective.getId())
                        .title(retrospective.getTitle())
                        .teamName(retrospective.getProject().getName())
                        .writtenDate(retrospective.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate())
                        .build())
                .collect(Collectors.toList());

        return DashboardRes.RetrospectiveListRes.builder()
                .retrospectives(retrospectiveInfos)
                .build();
    }
}
