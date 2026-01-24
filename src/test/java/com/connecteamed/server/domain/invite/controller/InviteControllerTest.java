package com.connecteamed.server.domain.invite.controller;

import com.connecteamed.server.domain.invite.code.InviteErrorCode;
import com.connecteamed.server.domain.invite.dto.InviteCodeRes;
import com.connecteamed.server.domain.invite.dto.ProjectJoinReq;
import com.connecteamed.server.domain.invite.service.InviteService;
import com.connecteamed.server.domain.project.code.ProjectErrorCode;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // 전체 애플리케이션 컨텍스트를 로드합니다.
@AutoConfigureMockMvc // MockMvc를 자동으로 설정합니다.
class InviteControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InviteService inviteService; // 서비스는 모킹하여 비즈니스 로직 분리


    @Test
    @WithMockUser(username = "test123")
    @DisplayName("초대 코드 발급 성공")
    void getInviteCode_Success() throws Exception {
        Long projectId = 1L;
        String loginId = "test123";
        InviteCodeRes response = new InviteCodeRes("a47ab466", Instant.now().plus(Duration.ofDays(1)));


        when(inviteService.getOrGenerateInviteCode(eq(projectId), eq(loginId))).thenReturn(response);

        mockMvc.perform(get("/api/invite/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.inviteCode").value("a47ab466"));
    }

    @Test
    @WithMockUser(username = "test123")
    @DisplayName("초대 코드 발급 실패 - 프로젝트가 존재하지 않음")
    void getInviteCode_Fail_NotFound() throws Exception {
        Long projectId = 99L;

        when(inviteService.getOrGenerateInviteCode(anyLong(), anyString()))
                .thenThrow(new GeneralException(ProjectErrorCode.PROJECT_NOT_FOUND));

        mockMvc.perform(get("/api/invite/{projectId}", projectId))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.code").value("PROJECT_NOT_FOUND"));
    }

    @Test
    @WithMockUser(username = "test123")
    @DisplayName("초대 코드 발급 실패 - 프로젝트 멤버가 아님")
    void getInviteCode_Fail_Unauthorized() throws Exception {
        Long projectId = 1L;

        when(inviteService.getOrGenerateInviteCode(anyLong(), anyString()))
                .thenThrow(new GeneralException(InviteErrorCode.INVITE_UNAUTHORIZED_MEMBER));

        mockMvc.perform(get("/api/invite/{projectId}", projectId))
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.code").value("INVITE_UNAUTHORIZED_MEMBER"))
                .andExpect(jsonPath("$.message").value("초대 코드 발급 권한이 없습니다."))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "test123") // 로그인 ID 통일
    @DisplayName("초대 코드 발급 실패 - projectId가 양수가 아닐 때 (Validation 실패)")
    void getInviteCode_Fail_InvalidProjectId() throws Exception {
        Long invalidProjectId = 0L;

        mockMvc.perform(get("/api/invite/{projectId}", invalidProjectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.code").value("COMMON400"))
                .andExpect(jsonPath("$.message").exists());
    }


    @Test
    @WithMockUser(username = "test123")
    @DisplayName("프로젝트 입장 성공")
    void joinProject_Success() throws Exception {
        ProjectJoinReq request = new ProjectJoinReq("VALID_CODE");

        doNothing().when(inviteService).joinProjectByCode(anyString(), eq("test123"));

        mockMvc.perform(post("/api/invite/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공하였습니다."));
    }

    @Test
    @WithMockUser(username = "test123")
    @DisplayName("입장 실패 - inviteCode가 비어있는 채로 전달 받앗을 때)")
    void joinProject_Fail_MissingInput() throws Exception {
        ProjectJoinReq request = new ProjectJoinReq("");
        mockMvc.perform(post("/api/invite/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.message").value("초대 코드는 필수 입력 항목입니다."))
                .andExpect(jsonPath("$.code").value("COMMON400")); // @Valid 검증 실패 시
    }

    @Test
    @WithMockUser(username = "test123")
    @DisplayName("입장 실패 - 유효하지 않은 코드")
    void joinProject_Fail_InvalidCode() throws Exception {
        ProjectJoinReq request = new ProjectJoinReq("WRONG_OR_EXPIRED");

        doThrow(new GeneralException(InviteErrorCode.INVALID_INVITE_CODE))
                .when(inviteService).joinProjectByCode(anyString(), anyString());

        mockMvc.perform(post("/api/invite/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.code").value(InviteErrorCode.INVALID_INVITE_CODE.getCode()));
    }

    @Test
    @WithMockUser(username = "test123")
    @DisplayName("입장 실패 - 만료된 코드")
    void joinProject_Fail_ExpiredCode() throws Exception {
        ProjectJoinReq request = new ProjectJoinReq("WRONG_OR_EXPIRED");

        doThrow(new GeneralException(InviteErrorCode.INVITE_CODE_EXPIRED))
                .when(inviteService).joinProjectByCode(anyString(), anyString());

        mockMvc.perform(post("/api/invite/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.code").value(InviteErrorCode.INVITE_CODE_EXPIRED.getCode()));
    }

    @Test
    @WithMockUser(username = "test123")
    @DisplayName("입장 실패 - 이미 해당 프로젝트의 멤버인 경우")
    void joinProject_Fail_AlreadyMember() throws Exception {
        ProjectJoinReq request = new ProjectJoinReq("VALID_CODE");

        doThrow(new GeneralException(InviteErrorCode.INVITE_ALREADY_INVITED))
                .when(inviteService).joinProjectByCode(anyString(), anyString());

        mockMvc.perform(post("/api/invite/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.code").value(InviteErrorCode.INVITE_ALREADY_INVITED.getCode()));
    }
}