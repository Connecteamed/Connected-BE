package com.connecteamed.server.domain.dashboard.controller;

import com.connecteamed.server.domain.dashboard.dto.DashboardRes;
import com.connecteamed.server.domain.dashboard.service.DashboardService;
import com.connecteamed.server.global.apiPayload.ApiResponse;
import com.connecteamed.server.global.apiPayload.code.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/retrospectives")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "대시보드 관련 API")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * 최근 회고 목록 조회
     * @return 회고 목록 응답
     */
    @GetMapping("/recent")
    @Operation(summary = "최근 회고 목록 조회", description = "최근에 작성된 회고 목록을 조회합니다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "회고 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ApiResponse<DashboardRes.RetrospectiveListRes> getRecentRetrospectives() {
        DashboardRes.RetrospectiveListRes response = dashboardService.getRecentRetrospectives();
        return ApiResponse.onSuccess(
                GeneralSuccessCode._OK,
                response,
                "회고 목록 조회에 성공했습니다"
        );
    }
}
