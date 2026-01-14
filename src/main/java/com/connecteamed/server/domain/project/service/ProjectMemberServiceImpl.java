package com.connecteamed.server.domain.project.service;

import com.connecteamed.server.domain.project.dto.ProjectMemberRes;
import com.connecteamed.server.domain.project.dto.ProjectMemberRoleUpdateReq;
import com.connecteamed.server.domain.project.entity.ProjectMember;
import com.connecteamed.server.domain.project.entity.ProjectMemberRole;
import com.connecteamed.server.domain.project.entity.ProjectRole;
import com.connecteamed.server.domain.project.repository.ProjectMemberRepository;
import com.connecteamed.server.domain.project.repository.ProjectRoleRepository;
import com.connecteamed.server.global.apiPayload.code.GeneralErrorCode;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRoleRepository projectRoleRepository;

    @Override
    public List<ProjectMemberRes> getProjectMembers(Long projectId) {
        List<ProjectMember> members = projectMemberRepository.findAllByProjectId(projectId);

        return members.stream()
                .map(this::toRes)
                .toList();
    }

    @Override
    @Transactional
    public ProjectMemberRes updateMemberRoles(Long projectId, Long projectMemberId, ProjectMemberRoleUpdateReq req) {
        ProjectMember pm = projectMemberRepository.findByIdAndProjectId(projectMemberId, projectId)
                .orElseThrow(() -> new GeneralException(
                        GeneralErrorCode.NOT_FOUND,
                        "프로젝트 팀원을 찾을 수 없습니다."
                ));

        List<Long> roleIds = (req.roleIds() == null) ? List.of() : req.roleIds();
        if (roleIds.isEmpty()) {
            // 역할 전체 해제(빈 배열 허용 정책일 때)
            pm.getRoles().clear(); // orphanRemoval=true면 기존 row 삭제됨
            return toRes(pm);
        }

        // 요청 roleIds 로 role 엔티티 로드
        List<ProjectRole> roles = projectRoleRepository.findAllById(roleIds);

        // 존재하지 않는 roleId 검증
        Set<Long> foundIds = roles.stream().map(ProjectRole::getId).collect(Collectors.toSet());
        List<Long> missing = roleIds.stream().filter(id -> !foundIds.contains(id)).toList();
        if (!missing.isEmpty()) {
            throw new GeneralException(
                    GeneralErrorCode.BAD_REQUEST,
                    "존재하지 않는 roleId가 포함되어 있습니다: " + missing
            );
        }

        // 기존 역할 삭제 후 재할당
        pm.getRoles().clear();
        for (ProjectRole r : roles) {
            pm.getRoles().add(
                    ProjectMemberRole.builder()
                            .projectMember(pm)
                            .role(r)
                            .build()
            );
        }

        // pm은 영속 상태라 보통 save 없어도 되지만, 명시적으로 저장해도 무방
        projectMemberRepository.save(pm);

        return toRes(pm);
    }

    private ProjectMemberRes toRes(ProjectMember pm) {
        List<ProjectMemberRes.RoleRes> roles = pm.getRoles().stream()
                .map(pmr -> new ProjectMemberRes.RoleRes(
                        pmr.getRole().getId(),
                        pmr.getRole().getRoleName()
                ))
                .toList();

        return new ProjectMemberRes(
                pm.getId(),
                pm.getMember().getId(),
                pm.getMember().getName(),
                roles
        );
    }
}
