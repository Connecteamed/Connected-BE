package com.connecteamed.server.domain.dashboard.controller;

import com.connecteamed.server.domain.dashboard.dto.DashboardRes;
import com.connecteamed.server.domain.dashboard.service.DashboardService;
import com.connecteamed.server.global.apiPayload.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("DashboardController 테스트")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DashboardService dashboardService;

    private DashboardRes.RetrospectiveListRes retrospectiveListRes;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성
        List<DashboardRes.RetrospectiveInfo> retrospectives = Arrays.asList(
                DashboardRes.RetrospectiveInfo.builder()
                        .id(101L)
                        .title("1주차 스프린트 회고")
                        .teamName("2025 신입 공모전")
                        .writtenDate(LocalDate.of(2025, 12, 14))
                        .build(),
                DashboardRes.RetrospectiveInfo.builder()
                        .id(102L)
                        .title("2주차 스프린트 회고")
                        .teamName("2025 신입 공모전")
                        .writtenDate(LocalDate.of(2025, 12, 14))
                        .build()
        );

        retrospectiveListRes = DashboardRes.RetrospectiveListRes.builder()
                .retrospectives(retrospectives)
                .build();
    }

    @Test
    @DisplayName("GET /api/retrospectives/recent - 최근 회고 목록 조회 성공")
    @WithMockUser(username = "writer@example.com")
    void testGetRecentRetrospectives_Success() throws Exception {
        // given
        when(dashboardService.getRecentRetrospectives("writer@example.com")).thenReturn(retrospectiveListRes);

        // when & then
        mockMvc.perform(get("/api/retrospectives/recent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("회고 목록 조회에 성공했습니다"))
                .andExpect(jsonPath("$.code").isEmpty())
                .andExpect(jsonPath("$.data.retrospectives").isArray())
                .andExpect(jsonPath("$.data.retrospectives.length()").value(2))
                .andExpect(jsonPath("$.data.retrospectives[0].id").value(101))
                .andExpect(jsonPath("$.data.retrospectives[0].title").value("1주차 스프린트 회고"))
                .andExpect(jsonPath("$.data.retrospectives[0].teamName").value("2025 신입 공모전"))
                .andExpect(jsonPath("$.data.retrospectives[0].writtenDate").value("2025-12-14"))
                .andExpect(jsonPath("$.data.retrospectives[1].id").value(102))
                .andExpect(jsonPath("$.data.retrospectives[1].title").value("2주차 스프린트 회고"));
    }

    @Test
    @DisplayName("GET /api/retrospectives/recent - 응답 구조 검증")
    @WithMockUser(username = "writer@example.com")
    void testGetRecentRetrospectives_ResponseStructure() throws Exception {
        // given
        when(dashboardService.getRecentRetrospectives("writer@example.com")).thenReturn(retrospectiveListRes);

        // when & then
        mockMvc.perform(get("/api/retrospectives/recent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("GET /api/retrospectives/recent - 빈 목록 조회")
    @WithMockUser(username = "writer@example.com")
    void testGetRecentRetrospectives_Empty() throws Exception {
        // given
        DashboardRes.RetrospectiveListRes emptyResponse = DashboardRes.RetrospectiveListRes.builder()
                .retrospectives(new ArrayList<>())
                .build();
        when(dashboardService.getRecentRetrospectives("writer@example.com")).thenReturn(emptyResponse);

        // when & then
        mockMvc.perform(get("/api/retrospectives/recent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.retrospectives").isArray())
                .andExpect(jsonPath("$.data.retrospectives.length()").value(0));
    }

    @Test
    @DisplayName("GET /api/retrospectives/recent - 요청 경로가 정확하고 HTTP 상태가 200인지 확인")
    @WithMockUser(username = "writer@example.com")
    void testGetRecentRetrospectives_HttpStatus() throws Exception {
        // given
        when(dashboardService.getRecentRetrospectives("writer@example.com")).thenReturn(retrospectiveListRes);

        // when & then
        mockMvc.perform(get("/api/retrospectives/recent"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/retrospectives/recent - JSON 필드 순서 검증 (status, data, message, code)")
    @WithMockUser(username = "writer@example.com")
    void testGetRecentRetrospectives_JsonFieldOrder() throws Exception {
        // given
        when(dashboardService.getRecentRetrospectives("writer@example.com")).thenReturn(retrospectiveListRes);

        // when
        MvcResult result = mockMvc.perform(get("/api/retrospectives/recent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // then - JSON 응답 문자열 검증으로 순서 확인
        String responseBody = result.getResponse().getContentAsString();
        int statusIndex = responseBody.indexOf("\"status\"");
        int dataIndex = responseBody.indexOf("\"data\"");
        int messageIndex = responseBody.indexOf("\"message\"");
        int codeIndex = responseBody.indexOf("\"code\"");

        assertTrue(statusIndex < dataIndex, "status가 data보다 먼저 와야 함");
        assertTrue(dataIndex < messageIndex, "data가 message보다 먼저 와야 함");
        assertTrue(messageIndex < codeIndex, "message가 code보다 먼저 와야 함");
    }

    @Test
    @DisplayName("GET /api/retrospectives/recent - RetrospectiveInfo 필드 검증")
    @WithMockUser(username = "writer@example.com")
    void testGetRecentRetrospectives_RetrospectiveInfoFields() throws Exception {
        // given
        when(dashboardService.getRecentRetrospectives("writer@example.com")).thenReturn(retrospectiveListRes);

        // when & then
        mockMvc.perform(get("/api/retrospectives/recent")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.retrospectives[0]").isMap())
                .andExpect(jsonPath("$.data.retrospectives[0].id").exists())
                .andExpect(jsonPath("$.data.retrospectives[0].title").exists())
                .andExpect(jsonPath("$.data.retrospectives[0].teamName").exists())
                .andExpect(jsonPath("$.data.retrospectives[0].writtenDate").exists());
    }
}

