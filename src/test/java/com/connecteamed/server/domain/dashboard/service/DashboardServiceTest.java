package com.connecteamed.server.domain.dashboard.service;

import com.connecteamed.server.domain.dashboard.dto.DashboardRes;
import com.connecteamed.server.domain.member.entity.Member;
import com.connecteamed.server.domain.member.enums.SocialType;
import com.connecteamed.server.domain.project.entity.Project;
import com.connecteamed.server.domain.project.entity.ProjectMember;
import com.connecteamed.server.domain.retrospective.entity.AiRetrospective;
import com.connecteamed.server.domain.retrospective.repository.RetrospectiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardService 테스트")
class DashboardServiceTest {

    @Mock
    private RetrospectiveRepository retrospectiveRepository;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비는 각 테스트 메서드에서 수행
    }

    @Test
    @DisplayName("최근 회고 목록을 성공적으로 조회한다")
    void testGetRecentRetrospectives_Success() {
        // given
        Member ownerMember = Member.builder()
                .id(1L)
                .publicId(UUID.randomUUID())
                .name("프로젝트 오너")
                .loginId("owner@example.com")
                .socialType(SocialType.KAKAO)
                .build();

        Member writerMember = Member.builder()
                .id(2L)
                .publicId(UUID.randomUUID())
                .name("회고 작성자")
                .loginId("writer@example.com")
                .socialType(SocialType.KAKAO)
                .build();

        Project project = Project.builder()
                .id(1L)
                .publicId(UUID.randomUUID())
                .owner(ownerMember)
                .name("2025 신입 공모전")
                .goal("최고의 팀 구성으로 상금 획득")
                .build();

        ProjectMember projectMember = ProjectMember.builder()
                .id(1L)
                .project(project)
                .member(writerMember)
                .build();

        AiRetrospective retrospective1 = AiRetrospective.builder()
                .id(101L)
                .publicId(UUID.randomUUID())
                .project(project)
                .writer(projectMember)
                .title("1주차 스프린트 회고")
                .projectResult("좋은 진행")
                .build();
        ReflectionTestUtils.setField(retrospective1, "createdAt", OffsetDateTime.now());

        AiRetrospective retrospective2 = AiRetrospective.builder()
                .id(102L)
                .publicId(UUID.randomUUID())
                .project(project)
                .writer(projectMember)
                .title("2주차 스프린트 회고")
                .projectResult("더 나은 진행")
                .build();
        ReflectionTestUtils.setField(retrospective2, "createdAt", OffsetDateTime.now().minusDays(1));

        List<AiRetrospective> retrospectives = new ArrayList<>();
        retrospectives.add(retrospective1);
        retrospectives.add(retrospective2);

        when(retrospectiveRepository.findRecentRetrospectivesByUsername("writer@example.com"))
                .thenReturn(retrospectives);

        // when
        DashboardRes.RetrospectiveListRes response = dashboardService.getRecentRetrospectives("writer@example.com");

        // then
        assertNotNull(response);
        assertNotNull(response.getRetrospectives());
        assertEquals(2, response.getRetrospectives().size());

        DashboardRes.RetrospectiveInfo first = response.getRetrospectives().getFirst();
        assertEquals(101L, first.getId());
        assertEquals("1주차 스프린트 회고", first.getTitle());
        assertEquals("2025 신입 공모전", first.getTeamName());
        assertNotNull(first.getWrittenDate());

        DashboardRes.RetrospectiveInfo second = response.getRetrospectives().get(1);
        assertEquals(102L, second.getId());
        assertEquals("2주차 스프린트 회고", second.getTitle());
        assertEquals("2025 신입 공모전", second.getTeamName());
    }

    @Test
    @DisplayName("회고가 없는 경우 빈 목록을 반환한다")
    void testGetRecentRetrospectives_Empty() {
        // given
        when(retrospectiveRepository.findRecentRetrospectives()).thenReturn(new ArrayList<>());

        // when
        DashboardRes.RetrospectiveListRes response = dashboardService.getRecentRetrospectives();

        // then
        assertNotNull(response);
        assertNotNull(response.getRetrospectives());
        assertTrue(response.getRetrospectives().isEmpty());
    }

    @Test
    @DisplayName("회고의 필드가 올바르게 매핑된다")
    void testGetRecentRetrospectives_FieldMapping() {
        // given
        Member ownerMember = Member.builder()
                .id(1L)
                .publicId(UUID.randomUUID())
                .name("프로젝트 오너")
                .loginId("owner@example.com")
                .socialType(SocialType.KAKAO)
                .build();

        Member writerMember = Member.builder()
                .id(2L)
                .publicId(UUID.randomUUID())
                .name("회고 작성자")
                .loginId("writer@example.com")
                .socialType(SocialType.KAKAO)
                .build();

        Project project = Project.builder()
                .id(1L)
                .publicId(UUID.randomUUID())
                .owner(ownerMember)
                .name("2025 신입 공모전")
                .goal("최고의 팀 구성으로 상금 획득")
                .build();

        ProjectMember projectMember = ProjectMember.builder()
                .id(1L)
                .project(project)
                .member(writerMember)
                .build();

        AiRetrospective retrospective = AiRetrospective.builder()
                .id(101L)
                .publicId(UUID.randomUUID())
                .project(project)
                .writer(projectMember)
                .title("1주차 스프린트 회고")
                .projectResult("좋은 진행")
                .build();
        ReflectionTestUtils.setField(retrospective, "createdAt", OffsetDateTime.now());

        List<AiRetrospective> retrospectives = new ArrayList<>();
        retrospectives.add(retrospective);

        when(retrospectiveRepository.findRecentRetrospectivesByUsername("writer@example.com"))
                .thenReturn(retrospectives);

        // when
        DashboardRes.RetrospectiveListRes response = dashboardService.getRecentRetrospectives("writer@example.com");
        DashboardRes.RetrospectiveInfo retrospectiveInfo = response.getRetrospectives().getFirst();

        // then - 필드 존재 여부 및 타입 검증
        assertNotNull(retrospectiveInfo.getId());
        assertNotNull(retrospectiveInfo.getTitle());
        assertNotNull(retrospectiveInfo.getTeamName());
        assertNotNull(retrospectiveInfo.getWrittenDate());

        assertInstanceOf(Long.class, retrospectiveInfo.getId());
        assertInstanceOf(String.class, retrospectiveInfo.getTitle());
        assertInstanceOf(String.class, retrospectiveInfo.getTeamName());
        assertInstanceOf(LocalDate.class, retrospectiveInfo.getWrittenDate());
    }
}

