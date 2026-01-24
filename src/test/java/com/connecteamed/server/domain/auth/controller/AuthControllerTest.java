package com.connecteamed.server.domain.auth.controller;

import com.connecteamed.server.domain.member.code.MemberErrorCode;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;
import com.connecteamed.server.global.auth.dto.AuthReqDTO;
import com.connecteamed.server.global.auth.dto.AuthResDTO;
import com.connecteamed.server.global.auth.exception.AuthException;
import com.connecteamed.server.global.auth.exception.code.AuthErrorCode;
import com.connecteamed.server.global.auth.service.AuthCommandService;
import com.connecteamed.server.global.auth.service.AuthQueryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthController 로그인 테스트")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthQueryService authQueryService;

    @MockBean
    private AuthCommandService authCommandService;

    @Test
    @DisplayName("로그인 성공 - 올바른 JSON 규격 확인")
    void login_Success() throws Exception {
        AuthReqDTO.LoginDTO request = new AuthReqDTO.LoginDTO("testId", "password123");
        Long testMemberId = 1L;
        AuthResDTO.LoginDTO response = new AuthResDTO.LoginDTO(
                testMemberId,
                "eyJhbGci...",
                "eyJhbGci...",
                "Bearer",
                14400L
        );

        when(authQueryService.login(any())).thenReturn(response);
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.memberId").value(1L))
                .andExpect(jsonPath("$.data.accessToken").value("eyJhbGci..."))
                .andExpect(jsonPath("$.data.refreshToken").value("eyJhbGci..."))
                .andExpect(jsonPath("$.data.grantType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(14400))
                .andExpect(jsonPath("$.message").value("로그인에 성공하였습니다."))
                .andExpect(jsonPath("$.code").value(Matchers.nullValue())); // 성공 시 code는 nul
    }

    @Test
    @DisplayName("로그인 실패 - ID/비밀번호 누락 (400 에러)")
    void login_Fail_Validation() throws Exception {
        AuthReqDTO.LoginDTO request = new AuthReqDTO.LoginDTO("testId", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest()) // @Valid 검증 실패
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.message").value("비밀번호는 로그인에 필수 입력 값입니다."))
                .andExpect(jsonPath("$.code").value("COMMON400"));
    }

    @Test
    @DisplayName("로그인 실패 - ID 누락 (400 에러)")
    void login_Fail_ID_Validation() throws Exception {
        AuthReqDTO.LoginDTO request = new AuthReqDTO.LoginDTO("", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest()) // @Valid 검증 실패
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.message").value("ID는 로그인에 필수 입력 값입니다."))
                .andExpect(jsonPath("$.code").value("COMMON400"));
    }


    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_Fail_IncorrectPassword() throws Exception {
        AuthReqDTO.LoginDTO request = new AuthReqDTO.LoginDTO("testId", "wrongPassword");

        when(authQueryService.login(any()))
                .thenThrow(new AuthException(AuthErrorCode.INCORRECT_PASSWORD));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized()) // 401 Unauthorized
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.message").value("비밀번호가 틀렸습니다"))
                .andExpect(jsonPath("$.code").value("INCORRECT_PASSWORD"));
    }


    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_Fail_MemberNotFound() throws Exception {
        AuthReqDTO.LoginDTO request = new AuthReqDTO.LoginDTO("nonExistentId", "password123");

        when(authQueryService.login(any()))
                .thenThrow(new GeneralException(MemberErrorCode.MEMBER_NOT_FOUND));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isNotFound()) // 401 Unauthorized
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.code").value("MEMBER_NOT_FOUND")) //
                .andExpect(jsonPath("$.message").value("해당 사용자를 찾지 못했습니다."));
    }


    @Test
    @DisplayName("회원가입 성공 - 모든 정보가 유효한 경우")
    void signUp_Success() throws Exception {
        AuthReqDTO.JoinDTO request = new AuthReqDTO.JoinDTO(
                "testUser",
                "user123!",
                "password123!"
        );

        Long testMemberId = 1L;
        AuthResDTO.JoinDTO response = new AuthResDTO.JoinDTO(testMemberId,"testUser");

        when(authCommandService.signup(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.memberId").value(1L))
                .andExpect(jsonPath("$.data.name").value("testUser"))
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
                .andExpect(jsonPath("$.code").value(Matchers.nullValue()));


    }

    @Test
    @DisplayName("회원가입 실패 - 아이디 누락")
    void signUp_Fail_Id_Missing() throws Exception {

        AuthReqDTO.JoinDTO request = new AuthReqDTO.JoinDTO("testUser123","", "password123!" );

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.message").value("ID는 회원가입에 필수 입력 값입니다."))
                .andExpect(jsonPath("$.code").value("COMMON400"));
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 누락")
    void signUp_Fail_Password_Missing() throws Exception {

        AuthReqDTO.JoinDTO request = new AuthReqDTO.JoinDTO("testUser", "testUser123", "");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.message").value("비밀번호는 회원가입에 필수 입력 값입니다."))
                .andExpect(jsonPath("$.code").value("COMMON400"));
    }

    @Test
    @DisplayName("회원가입 실패 - 이름 누락")
    void signUp_Fail_Name_Missing() throws Exception {
        AuthReqDTO.JoinDTO request = new AuthReqDTO.JoinDTO("","testUser", "password123!");

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.message").value("이름은 회원가입에 필수 입력 값입니다."))
                .andExpect(jsonPath("$.code").value("COMMON400"));
    }


    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 아이디")
    void signUp_Fail_DuplicateId() throws Exception {
        AuthReqDTO.JoinDTO request = new AuthReqDTO.JoinDTO(
                "testUser",
                "loginId123",
                "password123!"
        );

        when(authCommandService.signup(any()))
                .thenThrow(new AuthException(AuthErrorCode.DUPLICATE_LOGIN_ID));

        mockMvc.perform(post("/api/auth/signup") // 컨트롤러 경로와 일치
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict()) // 예외 처리기에 설정된 상태 코드 확인
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.message").value("이미 존재하는 아이디입니다."))
                .andExpect(jsonPath("$.code").value("DUPLICATE_LOGIN_ID")); // 에러 코드 검증
    }


    @Test
    @DisplayName("토큰 재발급 성공 - 유효한 리프레시 토큰")
    void reissue_Success() throws Exception {
        AuthReqDTO.ReissueDTO request = new AuthReqDTO.ReissueDTO("dGhpcyBpcyB... (기존 리프레시 토큰)");

        AuthResDTO.RefreshResultDTO response = new AuthResDTO.RefreshResultDTO(
                "new_access_token_eyJhbG...",
                "new_refresh_token_eyJhbG..."
        );

        when(authQueryService.reissue(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.message").value("재발급에 성공하였습니다."))
                .andExpect(jsonPath("$.code").value(Matchers.nullValue()));

    }


    @Test
    @DisplayName("토큰 재발급 실패 - 리프레시 토큰 만료 (401)")
    void reissue_Fail_Expired() throws Exception {
        AuthReqDTO.ReissueDTO request = new AuthReqDTO.ReissueDTO("expired_token...");

        when(authQueryService.reissue(any()))
                .thenThrow(new AuthException(AuthErrorCode.REFRESH_TOKEN_EXPIRED));

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.message").value("리프레시 토큰이 만료되었습니다. 다시 로그인해 주세요."))
                .andExpect(jsonPath("$.code").value("REFRESH_TOKEN_EXPIRED"));
    }


    @Test
    @DisplayName("토큰 재발급 실패 - DB에 존재하지 않는 리프레시 토큰 (401)")
    void reissue_Fail_InvalidToken() throws Exception {
        AuthReqDTO.ReissueDTO request = new AuthReqDTO.ReissueDTO("invalid_refresh_token_string");

        when(authQueryService.reissue(any()))
                .thenThrow(new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN));

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.data").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.message").value("유효하지 않은 리프레시 토큰입니다."))
                .andExpect(jsonPath("$.code").value("INVALID_REFRESH_TOKEN"));
    }






}
