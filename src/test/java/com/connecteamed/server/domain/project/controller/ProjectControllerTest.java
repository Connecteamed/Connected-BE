package com.connecteamed.server.domain.project.controller;

import com.connecteamed.server.domain.project.code.ProjectErrorCode;
import com.connecteamed.server.domain.project.dto.ProjectRes;
import com.connecteamed.server.domain.project.dto.ProjectUpdateReq;
import com.connecteamed.server.domain.project.enums.ProjectStatus;
import com.connecteamed.server.domain.project.service.ProjectService;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ProjectController 테스트")
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    private ProjectRes.CreateResponse createResponse;
    private ProjectRes.DetailResponse detailResponse;
    private ProjectRes.CloseResponse closeResponse;

    @BeforeEach
    void setUp() {
        Instant now = Instant.now();

        createResponse = ProjectRes.CreateResponse.builder()
                .projectId(1L)
                .createdAt(now)
                .build();

        detailResponse = ProjectRes.DetailResponse.builder()
                .projectId(1L)
                .name("UMC 7기")
                .goal("앱 런칭")
                .requiredRoleNames(Arrays.asList("DESIGNER", "SERVER", "ANDROID"))
                .build();

        closeResponse = ProjectRes.CloseResponse.builder()
                .projectId(1L)
                .status(ProjectStatus.COMPLETED)
                .closedAt(now)
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("프로젝트 생성 성공 - Multipart")
    void createProject_Success() throws Exception {
        // given
        String jsonData = objectMapper.writeValueAsString(
                new TestProjectCreateDto("UMC 7기", "앱 런칭", Arrays.asList("DESIGNER", "SERVER", "ANDROID"))
        );

        when(projectService.createProject(any(), any())).thenReturn(createResponse);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/projects")
                        .param("json", jsonData)
                        .with(request -> {
                            request.setContentType("multipart/form-data");
                            return request;
                        }))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.projectId").value(1L));
    }

    @Test
    @WithMockUser
    @DisplayName("프로젝트 생성 실패 - 프로젝트명 누락")
    void createProject_Fail_MissingName() throws Exception {
        // given
        String jsonData = objectMapper.writeValueAsString(
                new TestProjectCreateDto("", "앱 런칭", Arrays.asList("DESIGNER"))
        );

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/projects")
                        .param("json", jsonData)
                        .with(request -> {
                            request.setContentType("multipart/form-data");
                            return request;
                        }))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    @WithMockUser
    @DisplayName("프로젝트 생성 실패 - 프로젝트명 중복")
    void createProject_Fail_DuplicateName() throws Exception {
        // given
        String jsonData = objectMapper.writeValueAsString(
                new TestProjectCreateDto("UMC 7기", "앱 런칭", Arrays.asList("DESIGNER"))
        );

        when(projectService.createProject(any(), any()))
                .thenThrow(new GeneralException(ProjectErrorCode.PROJECT_NAME_ALREADY_EXISTS));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/projects")
                        .param("json", jsonData)
                        .with(request -> {
                            request.setContentType("multipart/form-data");
                            return request;
                        }))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    @WithMockUser
    @DisplayName("프로젝트 상세 조회 성공")
    void getProjectDetail_Success() throws Exception {
        // given
        when(projectService.getProjectDetail(1L)).thenReturn(detailResponse);

        // when & then
        mockMvc.perform(get("/api/projects/{projectId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.projectId").value(1L))
                .andExpect(jsonPath("$.data.name").value("UMC 7기"))
                .andExpect(jsonPath("$.data.goal").value("앱 런칭"))
                .andExpect(jsonPath("$.data.requiredRoleNames").isArray())
                .andExpect(jsonPath("$.data.requiredRoleNames.length()").value(3))
                .andExpect(jsonPath("$.message").value("프로젝트 수정 화면 조회"));
    }

    @Test
    @WithMockUser
    @DisplayName("프로젝트 상세 조회 실패 - 프로젝트 없음")
    void getProjectDetail_Fail_ProjectNotFound() throws Exception {
        // given
        when(projectService.getProjectDetail(999L))
                .thenThrow(new GeneralException(ProjectErrorCode.PROJECT_NOT_FOUND));

        // when & then
        mockMvc.perform(get("/api/projects/{projectId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    @WithMockUser
    @DisplayName("프로젝트 수정 성공")
    void updateProject_Success() throws Exception {
        // given
        ProjectUpdateReq updateReq = ProjectUpdateReq.builder()
                .name("UMC 8기")
                .goal("서비스 런칭")
                .requiredRoleNames(Arrays.asList("DESIGNER", "SERVER"))
                .build();

        when(projectService.updateProject(eq(1L), any(ProjectUpdateReq.class)))
                .thenReturn(createResponse);

        // when & then
        mockMvc.perform(patch("/api/projects/{projectId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.projectId").value(1L))
                .andExpect(jsonPath("$.message").value("프로젝트 수정에 성공했습니다"));
    }

    @Test
    @WithMockUser
    @DisplayName("프로젝트 수정 실패 - 프로젝트명 누락")
    void updateProject_Fail_MissingName() throws Exception {
        // given
        ProjectUpdateReq updateReq = ProjectUpdateReq.builder()
                .name("")
                .goal("서비스 런칭")
                .requiredRoleNames(Arrays.asList("DESIGNER"))
                .build();

        // when & then
        mockMvc.perform(patch("/api/projects/{projectId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    @WithMockUser
    @DisplayName("프로젝트 수정 실패 - 프로젝트 없음")
    void updateProject_Fail_ProjectNotFound() throws Exception {
        // given
        ProjectUpdateReq updateReq = ProjectUpdateReq.builder()
                .name("UMC 8기")
                .goal("서비스 런칭")
                .requiredRoleNames(Arrays.asList("DESIGNER"))
                .build();

        when(projectService.updateProject(eq(999L), any(ProjectUpdateReq.class)))
                .thenThrow(new GeneralException(ProjectErrorCode.PROJECT_NOT_FOUND));

        // when & then
        mockMvc.perform(patch("/api/projects/{projectId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));
    }

    @Test
    @WithMockUser
    @DisplayName("프로젝트 종료 성공")
    void closeProject_Success() throws Exception {
        // given
        when(projectService.closeProject(1L)).thenReturn(closeResponse);

        // when & then
        mockMvc.perform(patch("/api/projects/{projectId}/close", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.projectId").value(1L))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.message").value("프로젝트 종료에 성공했습니다"));
    }

    @Test
    @WithMockUser
    @DisplayName("프로젝트 종료 실패 - 프로젝트 없음")
    void closeProject_Fail_ProjectNotFound() throws Exception {
        // given
        when(projectService.closeProject(999L))
                .thenThrow(new GeneralException(ProjectErrorCode.PROJECT_NOT_FOUND));

        // when & then
        mockMvc.perform(patch("/api/projects/{projectId}/close", 999L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("error"));
    }

    // 테스트용 DTO 클래스
    private static class TestProjectCreateDto {
        public String name;
        public String goal;
        public java.util.List<String> requiredRoleNames;

        public TestProjectCreateDto(String name, String goal, java.util.List<String> requiredRoleNames) {
            this.name = name;
            this.goal = goal;
            this.requiredRoleNames = requiredRoleNames;
        }
    }
}

