package com.connecteamed.server.domain.member.controller;

import com.connecteamed.server.domain.member.dto.MemberRes;
import com.connecteamed.server.domain.member.service.MemberService;
import com.connecteamed.server.global.apiPayload.ApiResponse;
import com.connecteamed.server.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "사용 가능한 아이디",
                                            value = "{ \"status\": \"success\", \"data\": { \"loginId\": \"dongguk2026\", \"isAvailabe\": true }, \"message\": \"요청에 성공하였습니다.\", \"code\": \"COMMON200\" }"
                                    ),
                                    @ExampleObject(
                                            name = "중복된 아이디",
                                            value = "{ \"status\": \"success\", \"data\": { \"loginId\": \"existing_user\", \"isAvailable\": false }, \"message\": \"요청에 성공하였습니다.\", \"code\": \"COMMON200\" }"
                                    )
                            }
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "아이디 미입력 에러",
                                    value = "{ \"status\": \"error\", \"data\": null, \"message\": \"아이디를 입력해주세요.\", \"code\": \"COMMON400\" }"
                            )
                    )
            )
    })
    @GetMapping("/members/check-id")
    public ApiResponse<MemberRes.CheckIdResultDTO> checkId(
            @RequestParam ("loginId")
            @NotBlank(message = "아이디를 입력해주세요.")
            String loginId) {

        MemberRes.CheckIdResultDTO result = memberService.checkIdDuplication(loginId);

        return ApiResponse.onSuccess(GeneralSuccessCode._OK, result);
    }
}
