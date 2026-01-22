package com.connecteamed.server.domain.member.controller;

import com.connecteamed.server.domain.member.dto.MemberRes;
import com.connecteamed.server.domain.member.service.MemberService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("MemberController 로그인 테스트")
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("아이디 중복 확인 - 사용 가능한 경우")
    void checkId_Success_Available() throws Exception {
        String loginId = "userId123";

        MemberRes.CheckIdResultDTO response = new MemberRes.CheckIdResultDTO(true);

        when(memberService.checkIdDuplication(loginId)).thenReturn(response);

        mockMvc.perform(get("/api/members/check-id")
                        .param("loginId", loginId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.available").value(true))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.code").value(Matchers.nullValue()));
    }



    @Test
    @DisplayName("아이디 중복 확인 - 사용 불가한 경우")
    void checkId_Success_Not_Available() throws Exception {
        String loginId = "userId123";

        MemberRes.CheckIdResultDTO response = new MemberRes.CheckIdResultDTO(false);

        when(memberService.checkIdDuplication(loginId)).thenReturn(response);

        mockMvc.perform(get("/api/members/check-id")
                        .param("loginId", loginId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.available").value(false))
                .andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
                .andExpect(jsonPath("$.code").value(Matchers.nullValue()));
    }


    @Test
    @DisplayName("아이디 중복 확인 실패 - 아이디가 공백인 경우")
    void checkId_Fail_Blank() throws Exception {
        String loginId = " ";

        mockMvc.perform(get("/api/members/check-id")
                        .param("loginId", loginId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.code").value("COMMON400")) // 공통 에러 코드 규격
                .andExpect(jsonPath("$.message").value("아이디를 입력해주세요."));
    }


    @Test
    @DisplayName("아이디 중복 확인 실패 - 파라미터 누락")
    void checkId_Fail_Missing_Param() throws Exception {

        mockMvc.perform(get("/api/members/check-id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest()) // 400 Bad Request
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.code").value("COMMON400"))
                .andExpect(jsonPath("$.message").value("loginId 파라미터가 누락되었습니다."));

    }
}
