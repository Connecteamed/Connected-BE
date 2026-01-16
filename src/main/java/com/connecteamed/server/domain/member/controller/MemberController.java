package com.connecteamed.server.domain.member.controller;

import com.connecteamed.server.domain.member.dto.MemberRes;
import com.connecteamed.server.domain.member.service.MemberService;
import com.connecteamed.server.global.apiPayload.ApiResponse;
import com.connecteamed.server.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "아이디 중복 확인", description = "아이디 중복 여부를 확인하는 API입니다.")
    @GetMapping("/members/check-id")
    public ApiResponse<MemberRes.CheckIdResultDTO> checkId(
            @RequestParam ("loginId")
            @NotBlank(message = "아이디를 입력해주세요.")
            String loginId) {

        MemberRes.CheckIdResultDTO result = memberService.checkIdDuplication(loginId);

        return ApiResponse.onSuccess(GeneralSuccessCode._OK, result);
    }
}
