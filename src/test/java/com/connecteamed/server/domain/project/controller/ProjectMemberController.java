package com.connecteamed.server.domain.project.controller;

import com.connecteamed.server.domain.project.dto.ProjectMemberRoleUpdateReq;
import com.connecteamed.server.domain.project.service.ProjectMemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProjectMemberControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    ProjectMemberService projectMemberService;

    @InjectMocks
    ProjectMemberController projectMemberController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(projectMemberController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .setValidator(validator)
                .build();
    }

    @Test
    @DisplayName("GET /api/projects/{projectId}/members 는 200을 반환한다")
    void getMembers_ok() throws Exception {
        when(projectMemberService.getProjectMembers(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/projects/{projectId}/members", 1L))
                .andExpect(status().isOk());

        verify(projectMemberService, times(1)).getProjectMembers(1L);
    }

    @Test
    @DisplayName("GET /api/projects/{projectId}/roles 는 200을 반환한다")
    void getProjectRoles_ok() throws Exception {
        when(projectMemberService.getProjectRoles(1L)).thenReturn(null);

        mockMvc.perform(get("/api/projects/{projectId}/roles", 1L))
                .andExpect(status().isOk());

        verify(projectMemberService, times(1)).getProjectRoles(1L);
    }

    @Test
    @DisplayName("PATCH roles: roleIds 미전달({})이면 roleIds가 null로 바인딩되고 200을 반환한다")
    void patchRoles_omitRoleIds_bindsNull_ok() throws Exception {
        when(projectMemberService.updateMemberRoles(eq(1L), eq(10L), any(ProjectMemberRoleUpdateReq.class)))
                .thenReturn(null);

        mockMvc.perform(
                        patch("/api/projects/{projectId}/members/{projectMemberId}/roles", 1L, 10L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{}")
                )
                .andExpect(status().isOk());

        ArgumentCaptor<ProjectMemberRoleUpdateReq> captor = ArgumentCaptor.forClass(ProjectMemberRoleUpdateReq.class);
        verify(projectMemberService, times(1)).updateMemberRoles(eq(1L), eq(10L), captor.capture());

        ProjectMemberRoleUpdateReq req = captor.getValue();
        assertNotNull(req);
        assertNull(req.roleIds());
    }

    @Test
    @DisplayName("PATCH roles: roleIds 빈 배열([])이면 빈 리스트로 바인딩되고 200을 반환한다")
    void patchRoles_emptyArray_bindsEmpty_ok() throws Exception {
        when(projectMemberService.updateMemberRoles(eq(1L), eq(10L), any(ProjectMemberRoleUpdateReq.class)))
                .thenReturn(null);

        mockMvc.perform(
                        patch("/api/projects/{projectId}/members/{projectMemberId}/roles", 1L, 10L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"roleIds\":[]}")
                )
                .andExpect(status().isOk());

        ArgumentCaptor<ProjectMemberRoleUpdateReq> captor = ArgumentCaptor.forClass(ProjectMemberRoleUpdateReq.class);
        verify(projectMemberService, times(1)).updateMemberRoles(eq(1L), eq(10L), captor.capture());

        ProjectMemberRoleUpdateReq req = captor.getValue();
        assertNotNull(req);
        assertNotNull(req.roleIds());
        assertTrue(req.roleIds().isEmpty());
    }

    @Test
    @DisplayName("PATCH roles: roleIds가 있으면 그대로 바인딩되고 200을 반환한다")
    void patchRoles_withRoleIds_binds_ok() throws Exception {
        when(projectMemberService.updateMemberRoles(eq(1L), eq(10L), any(ProjectMemberRoleUpdateReq.class)))
                .thenReturn(null);

        mockMvc.perform(
                        patch("/api/projects/{projectId}/members/{projectMemberId}/roles", 1L, 10L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"roleIds\":[1,2]}")
                )
                .andExpect(status().isOk());

        ArgumentCaptor<ProjectMemberRoleUpdateReq> captor = ArgumentCaptor.forClass(ProjectMemberRoleUpdateReq.class);
        verify(projectMemberService, times(1)).updateMemberRoles(eq(1L), eq(10L), captor.capture());

        ProjectMemberRoleUpdateReq req = captor.getValue();
        assertNotNull(req);
        assertEquals(List.of(1L, 2L), req.roleIds());
    }

    @Test
    @DisplayName("PATCH roles: JSON이 깨져있으면 400을 반환한다")
    void patchRoles_invalidJson_badRequest() throws Exception {
        mockMvc.perform(
                        patch("/api/projects/{projectId}/members/{projectMemberId}/roles", 1L, 10L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"roleIds\":[1,}") // 깨진 JSON
                )
                .andExpect(status().isBadRequest());

        verify(projectMemberService, never()).updateMemberRoles(anyLong(), anyLong(), any());
    }
}
