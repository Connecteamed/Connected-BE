package com.connecteamed.server.domain.project.controller;

import com.connecteamed.server.domain.project.code.ProjectErrorCode;
import com.connecteamed.server.domain.project.code.ProjectSuccessCode;
import com.connecteamed.server.domain.project.dto.ProjectCreateReq;
import com.connecteamed.server.domain.project.dto.ProjectRes;
import com.connecteamed.server.domain.project.dto.ProjectUpdateReq;
import com.connecteamed.server.domain.project.service.ProjectService;
import com.connecteamed.server.global.apiPayload.ApiResponse;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;
import com.connecteamed.server.global.util.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Project", description = "프로젝트 관련 API")
public class ProjectController {

    private final ProjectService projectService;
    private final ObjectMapper objectMapper;

    /**
     * 프로젝트 생성
     * Content-Type: Multipart/form-data
     * - Part 1: image (MultipartFile, optional)
     * - Part 2: json (JSON string with name, goal, requiredRoleNames)
     *
     * @param image 프로젝트 이미지 (선택)
     * @param json 프로젝트 정보 JSON 문자열
     * @return 생성된 프로젝트 정보
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "프로젝트 생성",
            description = "Multipart/form-data로 프로젝트를 생성합니다. "
                    + "Part 1: image (MultipartFile, optional), "
                    + "Part 2: json (JSON 문자열)"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "프로젝트 생성 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<ProjectRes.CreateResponse>> createProject(
            @RequestParam(value = "image", required = false)
            @Parameter(description = "프로젝트 이미지")
            MultipartFile image,

            @RequestParam(value = "json")
            @Parameter(description = "프로젝트 정보 JSON 문자열")
            String json
    ) {
        try {
            // 1. JSON 문자열을 DTO로 변환
            ProjectCreateReq createReq = objectMapper.readValue(json, ProjectCreateReq.class);

            // 2. 이미지 바인딩 (multipart file 따로 받음)
            if (image != null) {
                createReq = ProjectCreateReq.builder()
                        .image(image)
                        .name(createReq.getName())
                        .goal(createReq.getGoal())
                        .requiredRoleNames(createReq.getRequiredRoleNames())
                        .build();
            }

            // 3. 필수 필드 검증
            if (createReq.getName() == null || createReq.getName().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.onFailure(ProjectErrorCode.PROJECT_NAME_REQUIRED));
            }

            if (createReq.getGoal() == null || createReq.getGoal().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.onFailure(ProjectErrorCode.PROJECT_GOAL_REQUIRED));
            }

            if (createReq.getRequiredRoleNames() == null || createReq.getRequiredRoleNames().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.onFailure(ProjectErrorCode.PROJECT_REQUIRED_ROLES_REQUIRED));
            }

            // 4. 사용자 ID 확인 (JWT 인증 또는 테스트 모드)
            String loginId = SecurityUtil.getCurrentLoginId();
            log.info("[ProjectController] Retrieved loginId from SecurityUtil: {}", loginId);

            // 5. 프로젝트 생성
            ProjectRes.CreateResponse response = projectService.createProject(createReq, loginId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.onSuccess(
                            ProjectSuccessCode.CREATED,
                            response,
                            "프로젝트 생성에 성공했습니다"
                    ));

        } catch (IOException e) {
            log.error("[ProjectController] IOException occurred: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.onFailure(ProjectErrorCode.INVALID_REQUEST));
        } catch (GeneralException e) {
            log.error("[ProjectController] GeneralException occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getCode().getStatus())
                    .body(ApiResponse.onFailure(e.getCode()));
        } catch (Exception e) {
            log.error("[ProjectController] Unexpected exception occurred: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure(ProjectErrorCode.INVALID_REQUEST));
        }
    }

    /**
     * 프로젝트 상세 조회
     * @param projectId 프로젝트 ID
     * @return 프로젝트 상세 정보
     */
    @GetMapping("/{projectId}")
    @Operation(
            summary = "프로젝트 상세 조회",
            description = "프로젝트 ID로 프로젝트의 상세 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "프로젝트 조회 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "프로젝트를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<ProjectRes.DetailResponse>> getProjectDetail(
            @PathVariable
            @Parameter(description = "프로젝트 ID", example = "7")
            Long projectId
    ) {
        try {
            log.info("[ProjectController] getProjectDetail called with projectId: {}", projectId);

            ProjectRes.DetailResponse response = projectService.getProjectDetail(projectId);

            return ResponseEntity.ok(
                    ApiResponse.onSuccess(
                            ProjectSuccessCode.OK,
                            response,
                            "프로젝트 수정 화면 조회"
                    )
            );
        } catch (GeneralException e) {
            log.error("[ProjectController] GeneralException occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getCode().getStatus())
                    .body(ApiResponse.onFailure(e.getCode()));
        } catch (Exception e) {
            log.error("[ProjectController] Unexpected exception occurred: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure(ProjectErrorCode.INVALID_REQUEST));
        }
    }

    /**
     * 프로젝트 수정
     * @param projectId 프로젝트 ID
     * @param updateReq 프로젝트 수정 요청
     * @return 수정된 프로젝트 정보
     */
    @PatchMapping("/{projectId}")
    @Operation(
            summary = "프로젝트 수정",
            description = "프로젝트 ID로 프로젝트 정보를 수정합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "프로젝트 수정 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "프로젝트를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "프로젝트명 중복",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    public ResponseEntity<ApiResponse<ProjectRes.CreateResponse>> updateProject(
            @PathVariable
            @Parameter(description = "프로젝트 ID", example = "7")
            Long projectId,

            @RequestBody
            @Parameter(description = "프로젝트 수정 요청")
            ProjectUpdateReq updateReq
    ) {
        try {
            log.info("[ProjectController] updateProject called with projectId: {}", projectId);

            // 필수 필드 검증
            if (updateReq.getName() == null || updateReq.getName().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.onFailure(ProjectErrorCode.PROJECT_NAME_REQUIRED));
            }

            if (updateReq.getGoal() == null || updateReq.getGoal().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.onFailure(ProjectErrorCode.PROJECT_GOAL_REQUIRED));
            }

            if (updateReq.getRequiredRoleNames() == null || updateReq.getRequiredRoleNames().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.onFailure(ProjectErrorCode.PROJECT_REQUIRED_ROLES_REQUIRED));
            }

            ProjectRes.CreateResponse response = projectService.updateProject(projectId, updateReq);

            return ResponseEntity.ok(
                    ApiResponse.onSuccess(
                            ProjectSuccessCode.OK,
                            response,
                            "프로젝트 수정에 성공했습니다"
                    )
            );
        } catch (GeneralException e) {
            log.error("[ProjectController] GeneralException occurred: {}", e.getMessage(), e);
            return ResponseEntity.status(e.getCode().getStatus())
                    .body(ApiResponse.onFailure(e.getCode()));
        } catch (Exception e) {
            log.error("[ProjectController] Unexpected exception occurred: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.onFailure(ProjectErrorCode.INVALID_REQUEST));
        }
    }
}
