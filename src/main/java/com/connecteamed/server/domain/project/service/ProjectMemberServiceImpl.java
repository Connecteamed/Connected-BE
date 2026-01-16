package com.connecteamed.server.domain.project.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.connecteamed.server.domain.project.dto.ProjectMemberRes;
import com.connecteamed.server.domain.project.dto.ProjectMemberRoleUpdateReq;
import com.connecteamed.server.domain.project.dto.ProjectRoleListRes;
import com.connecteamed.server.domain.project.entity.ProjectMember;
import com.connecteamed.server.domain.project.entity.ProjectMemberRole;
import com.connecteamed.server.domain.project.entity.ProjectRole;
import com.connecteamed.server.domain.project.repository.ProjectMemberRepository;
import com.connecteamed.server.domain.project.repository.ProjectRequiredRoleRepository;
import com.connecteamed.server.domain.project.repository.ProjectRoleRepository;
import com.connecteamed.server.global.apiPayload.code.GeneralErrorCode;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRoleRepository projectRoleRepository;
    private final ProjectRequiredRoleRepository projectRequiredRoleRepository;

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
        log.info("updateMemberRoles called. projectId={}, projectMemberId={}, req={}",
                projectId, projectMemberId, req);

        ProjectMember pm = projectMemberRepository.findByIdAndProjectId(projectMemberId, projectId)
                .orElseThrow(() -> new GeneralException(
                        GeneralErrorCode.NOT_FOUND,
                        "프로젝트 팀원을 찾을 수 없습니다."
                ));

        // 요청 roleIds (null 방어 + null 요소 제거 + 중복 제거)
        List<Long> requestedRoleIds = (req.roleIds() == null) ? List.of() : req.roleIds();
        Set<Long> requestedRoleIdSet = requestedRoleIds.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new)); // 순서 유지

        // 역할 전체 해제
        if (requestedRoleIdSet.isEmpty()) {
            pm.getRoles().clear(); // orphanRemoval=true면 DB row 삭제
            return toRes(pm);
        }

        // role 엔티티 로드
        List<ProjectRole> roles = projectRoleRepository.findAllById(requestedRoleIdSet);

        // 존재하지 않는 roleId 검증
        Set<Long> foundIds = roles.stream().map(ProjectRole::getId).collect(Collectors.toSet());
        List<Long> missing = requestedRoleIdSet.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();

        if (!missing.isEmpty()) {
            throw new GeneralException(
                    GeneralErrorCode.BAD_REQUEST,
                    "존재하지 않는 roleId가 포함되어 있습니다: " + missing
            );
        }

        // 현재 보유 roleId 집합
        Set<Long> currentRoleIds = pm.getRoles().stream()
                .map(r -> r.getRole().getId())
                .collect(Collectors.toSet());


        // 1) 요청에 없는 기존 역할 삭제
        pm.getRoles().removeIf(r -> !requestedRoleIdSet.contains(r.getRole().getId()));

        // 2) 기존에 없는 역할만 추가
        for (ProjectRole r : roles) {
            if (!currentRoleIds.contains(r.getId())) {
                pm.getRoles().add(
                        ProjectMemberRole.builder()
                                .projectMember(pm)
                                .role(r)
                                .build()
                );
            }
        }

        // 영속 상태면 save 없어도 반영됩니다(더티체킹).
        // 명시적으로 호출하고 싶으면 아래 한 줄을 써도 됩니다.
        projectMemberRepository.save(pm);
        projectMemberRepository.flush();

        return toRes(pm);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectRoleListRes getProjectRoles(Long projectId) {
        List<Object[]> rows = projectRequiredRoleRepository.findRequiredRoles(projectId);

        List<ProjectRoleListRes.RoleItem> roles = rows.stream()
                .map(r -> new ProjectRoleListRes.RoleItem(
                        (Long) r[0],
                        (String) r[1]
                ))
                .toList();

        return new ProjectRoleListRes(roles);
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
