package com.connecteamed.server.domain.team.controller;


import com.connecteamed.server.domain.team.code.TeamSuccessCode;
import com.connecteamed.server.domain.team.dto.TeamListRes;
import com.connecteamed.server.domain.team.service.TeamService;
import com.connecteamed.server.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Team", description = "팀 관련 API")
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "내가 참여한 프로젝트(팀) 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "팀 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "조회 성공 예시",
                                    value = """
                {
                  "status": "success",
                  "data": {
                    "teams": [
                      { "teamId": 1, "name": "connecteamed1" },
                      { "teamId": 2, "name": "connecteamed2" }
                    ]
                  },
                  "message": "요청에 성공하였습니다.",
                  "code": null
                }
                """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "조회 실패 예시",
                                    value = """
                {
                  "status": "error",
                  "data": null,
                  "message": "인증 정보가 존재하지 않습니다.",
                  "code": "EMPTHY_AUTHENTICATION"
                }
                """
                            )
                    )),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음 (MEMBER_NOT_FOUND)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "조회 실패 예시",
                                    value = """
                {
                  "status": "error",
                  "data": null,
                  "message": "해당 사용자를 찾지 못했습니다.",
                  "code": "MEMBER_NOT_FOUND"
                }
                """
                            )
                    ))
    })
    @GetMapping("/my")
    public ApiResponse<TeamListRes.TeamDataList> getMyTeams() {
        TeamListRes.TeamDataList response = teamService.getMyProjectTeams();
        return ApiResponse.onSuccess(TeamSuccessCode.OK,response);
    }
}
