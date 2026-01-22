package com.connecteamed.server.domain.team;


import com.connecteamed.server.domain.member.code.MemberErrorCode;
import com.connecteamed.server.domain.team.dto.TeamListRes;
import com.connecteamed.server.domain.team.service.TeamService;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;
import com.connecteamed.server.global.auth.exception.AuthException;
import com.connecteamed.server.global.auth.exception.code.AuthErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Team 통합 테스트")
public class TeamControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TeamService teamService;

    @Test
    @WithMockUser
    @DisplayName("나의 프로젝트 목록 조회 API 성공")
    void getMyTeams_Success() throws Exception {

        TeamListRes.TeamInfo team1 = TeamListRes.TeamInfo.builder()
                .teamId(1L)
                .name("test team 1")
                .build();

        TeamListRes.TeamInfo team2 = TeamListRes.TeamInfo.builder()
                .teamId(2L)
                .name("test team 2")
                .build();

        TeamListRes.TeamDataList response = new TeamListRes.TeamDataList(List.of(team1,team2));

        given(teamService.getMyProjectTeams()).willReturn(response);

        mockMvc.perform(get("/api/teams/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.teams.length()").value(2))
                .andExpect(jsonPath("$.data.teams").isArray())
                .andExpect(jsonPath("$.data.teams[0].teamId").value(1L))
                .andExpect(jsonPath("$.data.teams[0].name").value("test team 1"))
                .andExpect(jsonPath("$.data.teams[1].teamId").value(2L))
                .andExpect(jsonPath("$.data.teams[1].name").value("test team 2"));
    }


    @Test
    @WithMockUser
    @DisplayName("나의 프로젝트 목록 조회 실패 - 회원을 찾을 수 없는 경우")
    void getMyTeams_MemberNotFound_Failure() throws Exception {

        given(teamService.getMyProjectTeams())
                .willThrow(new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

        mockMvc.perform(get("/api/teams/my"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("해당 사용자를 찾지 못했습니다."))
                ;
    }


    @Test
    @WithMockUser
    @DisplayName("나의 프로젝트 목록 조회 실패 - 인증 실패(로그인 필요한 경우)")
    void getMyTeams_Empty_Authentication() throws Exception {

        given(teamService.getMyProjectTeams())
                .willThrow(new AuthException(AuthErrorCode.EMPTY_AUTHENTICATION));

        mockMvc.perform(get("/api/teams/my"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").exists())
        ;
    }
}
