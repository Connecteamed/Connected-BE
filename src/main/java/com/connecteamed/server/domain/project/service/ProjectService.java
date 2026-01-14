package com.connecteamed.server.domain.project.service;

import com.connecteamed.server.domain.member.entity.Member;
import com.connecteamed.server.domain.member.repository.MemberRepository;
import com.connecteamed.server.domain.project.code.ProjectErrorCode;
import com.connecteamed.server.domain.project.dto.ProjectCreateReq;
import com.connecteamed.server.domain.project.dto.ProjectRes;
import com.connecteamed.server.domain.project.entity.Project;
import com.connecteamed.server.domain.project.entity.ProjectRequiredRole;
import com.connecteamed.server.domain.project.entity.ProjectRole;
import com.connecteamed.server.domain.project.repository.ProjectRepository;
import com.connecteamed.server.domain.project.repository.ProjectRequiredRoleRepository;
import com.connecteamed.server.domain.project.repository.ProjectRoleRepository;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;
import com.connecteamed.server.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final ProjectRequiredRoleRepository projectRequiredRoleRepository;
    private final MemberRepository memberRepository;
    private final Optional<S3Uploader> s3Uploader;

    /**
     * 프로젝트 생성
     * @param createReq 프로젝트 생성 요청 (이미지, 이름, 목표, 필요 역할)
     * @param loginId 로그인한 사용자 ID (JWT에서 추출)
     * @return 생성된 프로젝트 정보
     */
    public ProjectRes.CreateResponse createProject(ProjectCreateReq createReq, String loginId) {
        log.info("[ProjectService] createProject called with loginId: {}", loginId);

        // 0. 프로젝트명 중복 체크
        log.debug("[ProjectService] Checking if project name already exists: {}", createReq.getName());
        projectRepository.findByName(createReq.getName()).ifPresent(project -> {
            log.error("[ProjectService] Project name already exists: {}", createReq.getName());
            throw new GeneralException(ProjectErrorCode.PROJECT_NAME_ALREADY_EXISTS);
        });

        // 1. 회원 존재 여부 확인
        log.debug("[ProjectService] Finding member with loginId: {}", loginId);
        Member owner = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> {
                    log.error("[ProjectService] Member not found with loginId: {}", loginId);
                    return new GeneralException(ProjectErrorCode.MEMBER_NOT_FOUND);
                });
        log.info("[ProjectService] Member found: id={}, name={}", owner.getId(), owner.getName());

        String imageUrl = null;
        MultipartFile image = createReq.getImage();
        if (image != null && !image.isEmpty() && s3Uploader.isPresent()) {
            try {
                log.debug("[ProjectService] Uploading image: {}", image.getOriginalFilename());
                imageUrl = s3Uploader.get().upload(image, "project");
                log.info("[ProjectService] Image uploaded successfully: {}", imageUrl);
            } catch (IOException e) {
                log.error("[ProjectService] Image upload failed: ", e);
                throw new GeneralException(ProjectErrorCode.INVALID_REQUEST, "이미지 업로드 실패: " + e.getMessage());
            }
        }

        // 2. 프로젝트 생성
        log.debug("[ProjectService] Creating project with name: {}", createReq.getName());
        Project project = Project.builder()
                .name(createReq.getName())
                .goal(createReq.getGoal())
                .owner(owner)
                .imageUrl(imageUrl)
                .build();

        Project savedProject = projectRepository.save(project);
        log.info("[ProjectService] Project created successfully: id={}, name={}", savedProject.getId(), savedProject.getName());

        // 3. 필요 역할 등록
        if (createReq.getRequiredRoleNames() != null && !createReq.getRequiredRoleNames().isEmpty()) {
            log.debug("[ProjectService] Registering required roles: {}", createReq.getRequiredRoleNames());
            for (String roleName : createReq.getRequiredRoleNames()) {
                // 역할명으로 ProjectRole 조회
                log.debug("[ProjectService] Finding ProjectRole: {}", roleName);
                ProjectRole projectRole = projectRoleRepository.findByRoleName(roleName)
                        .orElseThrow(() -> {
                            log.error("[ProjectService] ProjectRole not found: {}", roleName);
                            return new GeneralException(ProjectErrorCode.ROLE_NOT_FOUND);
                        });

                // ProjectRequiredRole 생성 및 저장
                ProjectRequiredRole requiredRole = ProjectRequiredRole.builder()
                        .project(savedProject)
                        .projectRole(projectRole)
                        .build();

                projectRequiredRoleRepository.save(requiredRole);
                log.debug("[ProjectService] Required role registered: {}", roleName);
            }
        }

        // 4. 응답 반환
        log.info("[ProjectService] Returning CreateResponse: projectId={}", savedProject.getId());
        return ProjectRes.CreateResponse.builder()
                .projectId(savedProject.getId())
                .createdAt(savedProject.getCreatedAt())
                .build();
    }
}
