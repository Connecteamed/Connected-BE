package com.connecteamed.server.domain.myPage.controller;


import com.connecteamed.server.domain.myPage.service.MyPageRetrospectiveService;
import com.connecteamed.server.domain.myPage.dto.MyPageRetrospectiveRes;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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
@AutoConfigureMockMvc
@DisplayName("MemberRetrospectiveController 통합 테스트")
public class MyPageRetrospectiveControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MyPageRetrospectiveService retrospectiveService;

    @Test
    @WithMockUser(username = "test_user")
    @DisplayName("나의 회고 목록 조회 - 성공")
    void getMyRetrospectives_Success() throws Exception {
        MyPageRetrospectiveRes.RetrospectiveInfo retroData = MyPageRetrospectiveRes.RetrospectiveInfo.builder()
                .id(55L)
                .title("1주차 회고")
                .createdAt(Instant.now())
                .build();

        MyPageRetrospectiveRes.RetrospectiveList response = MyPageRetrospectiveRes.RetrospectiveList.builder()
                .retrospectives(List.of(retroData))
                .build();

        when(retrospectiveService.getMyRetrospectives()).thenReturn(response);

        mockMvc.perform(get("/api/mypage/retrospectives")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.retrospectives[0].title").value("1주차 회고"))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.code").value(Matchers.nullValue()));
    }

    @Test
    @WithMockUser(username = "test_user")
    @DisplayName("나의 회고 삭제 - 성공")
    void deleteRetrospective_Success() throws Exception {

        Long retrospectiveId = 55L;
        doNothing().when(retrospectiveService).deleteRetrospective(retrospectiveId);

        mockMvc.perform(delete("/api/mypage/retrospectives/{retrospectiveId}", retrospectiveId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("회고 삭제에 성공했습니다."));
    }
}
