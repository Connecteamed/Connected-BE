package com.connecteamed.server.domain.myPage.controller;

import com.connecteamed.server.domain.myPage.dto.MyPageProjectListRes;
import com.connecteamed.server.domain.myPage.service.MyPageProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // MockMvc를 자동 설정해줍니다.
@Transactional // 테스트 후 DB 데이터를 롤백하기 위해 추가 (필요 시)
@DisplayName("MemberProjectController 통합 테스트")
class MyPageProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 실제 서비스 대신 Mock을 사용하고 싶다면 @MockBean을 유지합니다.
    @MockBean
    private MyPageProjectService myPageProjectService;


    @Test
    @WithMockUser(username = "test_user")
    @DisplayName("완료한 프로젝트 목록 조회 - 성공")
    void getCompletedProjects_Success() throws Exception {
        MyPageProjectListRes.CompletedProjectData projectData = MyPageProjectListRes.CompletedProjectData.builder()
                .id(101L)
                .name("완료된 프로젝트")
                .roles(List.of("Back-end"))
                .createdAt(Instant.now())
                .build();

        MyPageProjectListRes.CompletedProjectList response = MyPageProjectListRes.CompletedProjectList.builder()
                .projects(List.of(projectData))
                .build();

        when(myPageProjectService.getMyCompletedProjects()).thenReturn(response);

        mockMvc.perform(get("/api/mypage/projects/completed")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.projects[0].name").value("완료된 프로젝트"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."));
    }


    @Test
    @WithMockUser(username = "test_user")
    @DisplayName("완료한 프로젝트 삭제 - 성공")
    void deleteCompletedProject_Success() throws Exception {
        Long projectId = 101L;

        doNothing().when(myPageProjectService).deleteCompletedProject(projectId);

        mockMvc.perform(delete("/api/mypage/projects/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."));

    }
}
