package com.connecteamed.server.domain.team;

import com.connecteamed.server.domain.member.entity.Member;
import com.connecteamed.server.domain.member.repository.MemberRepository;
import com.connecteamed.server.domain.project.entity.Project;
import com.connecteamed.server.domain.project.entity.ProjectMember;
import com.connecteamed.server.domain.project.repository.ProjectMemberRepository;
import com.connecteamed.server.domain.team.dto.TeamListRes;
import com.connecteamed.server.domain.team.service.TeamService;
import com.connecteamed.server.global.util.SecurityUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @InjectMocks
    private TeamService teamService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    private static MockedStatic<SecurityUtil> mockedSecurityUtil;

    @BeforeAll
    static void beforeAll() {
        mockedSecurityUtil = mockStatic(SecurityUtil.class);
    }

    @AfterAll
    static void afterAll() {
        mockedSecurityUtil.close();
    }

    @Test
    @DisplayName("나의 프로젝트 목록 조회 성공 - 참여 중인 프로젝트가 있을 때")
    void getMyProjectTeams_Success() {
        String loginId = "testUser";
        Long memberId = 1L;

        Member member = Member.builder().id(memberId).loginId(loginId).build();
        Project project = Project.builder().id(10L).name("테스트 프로젝트").build();
        ProjectMember projectMember = ProjectMember.builder().project(project).member(member).build();

        given(SecurityUtil.getCurrentLoginId()).willReturn(loginId);
        given(memberRepository.findByLoginId(loginId)).willReturn(Optional.of(member));
        given(projectMemberRepository.findAllByMemberIdWithProject(memberId))
                .willReturn(List.of(projectMember));

        TeamListRes.TeamDataList result = teamService.getMyProjectTeams();

        assertThat(result.getTeams()).hasSize(1);
        assertThat(result.getTeams().get(0).getName()).isEqualTo("테스트 프로젝트");
        assertThat(result.getTeams().get(0).getTeamId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("나의 프로젝트 목록 조회 성공 - 참여 중인 프로젝트가 없을 때 빈 배열 반환확인 테스트")
    void getMyProjectTeams_Empty() {
        String loginId = "noProjectUser";
        Long memberId = 2L;
        Member member = Member.builder().id(memberId).loginId(loginId).build();

        given(SecurityUtil.getCurrentLoginId()).willReturn(loginId);
        given(memberRepository.findByLoginId(loginId)).willReturn(Optional.of(member));
        given(projectMemberRepository.findAllByMemberIdWithProject(memberId)).willReturn(List.of());

        TeamListRes.TeamDataList result = teamService.getMyProjectTeams();

        assertThat(result.getTeams()).isEmpty(); // 빈 배열 반환 확인
    }
}
