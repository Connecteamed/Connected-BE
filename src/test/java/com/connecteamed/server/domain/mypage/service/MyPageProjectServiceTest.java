package com.connecteamed.server.domain.mypage.service;

import com.connecteamed.server.domain.member.entity.Member;
import com.connecteamed.server.domain.member.repository.MemberRepository;
import com.connecteamed.server.domain.mypage.code.MyPageErrorCode;
import com.connecteamed.server.domain.mypage.dto.MyPageProjectListRes;
import com.connecteamed.server.domain.project.entity.Project;
import com.connecteamed.server.domain.project.entity.ProjectMember;
import com.connecteamed.server.domain.project.enums.ProjectStatus;
import com.connecteamed.server.domain.project.repository.ProjectMemberRepository;
import com.connecteamed.server.domain.project.repository.ProjectRepository;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;
import com.connecteamed.server.global.util.SecurityUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MyPageProjectServiceTest {

    @InjectMocks
    private MyPageProjectService myPageProjectService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    private static MockedStatic<SecurityUtil> mockedSecurityUtil;

    @BeforeAll
    static void setup() {
        mockedSecurityUtil = mockStatic(SecurityUtil.class);
    }

    @AfterAll
    static void tearDown() {
        mockedSecurityUtil.close();
    }

    @Test
    @DisplayName("나의 완료된 프로젝트 목록 조회 성공")
    void getMyCompletedProjects_Filtering_Success() {
        //given
        String loginId = "test_user";
        Member member = Member.builder().id(1L).loginId(loginId).build();

        Project completedProject = Project.builder()
                .id(101L).name("완료된 프로젝트").status(ProjectStatus.COMPLETED).build();
        ProjectMember pm1 = ProjectMember.builder().project(completedProject).member(member).build();

        Project progressingProject = Project.builder()
                .id(102L).name("진행 중인 프로젝트").status(ProjectStatus.IN_PROGRESS).build();
        ProjectMember pm2 = ProjectMember.builder().project(progressingProject).member(member).build();

        given(SecurityUtil.getCurrentLoginId()).willReturn(loginId);
        given(memberRepository.findByLoginId(loginId)).willReturn(Optional.of(member));
        given(projectMemberRepository.findAllByMember(member)).willReturn(List.of(pm1, pm2));

        //When
        MyPageProjectListRes.CompletedProjectList result = myPageProjectService.getMyCompletedProjects();

        //then
        assertThat(result.getProjects()).hasSize(1);
        assertThat(result.getProjects().get(0).getName()).isEqualTo("완료된 프로젝트");
        assertThat(result.getProjects().get(0).getId()).isEqualTo(101L);


        boolean hasProgressing = result.getProjects().stream()
                .anyMatch(p -> p.getId().equals(102L));
        assertThat(hasProgressing).isFalse();
    }


    @Test
    @DisplayName("프로젝트 삭제 성공")
    void deleteCompletedProject_Success() {
        // 1. 준비 (Given)
        String loginId = "owner_user";
        Long projectId = 101L;

        Member owner = Member.builder().id(1L).loginId(loginId).build();
        Project project = Project.builder()
                .id(projectId)
                .owner(owner)
                .status(ProjectStatus.COMPLETED)
                .build();


        given(SecurityUtil.getCurrentLoginId()).willReturn(loginId);
        given(memberRepository.findByLoginId(loginId)).willReturn(Optional.of(owner));
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

        myPageProjectService.deleteCompletedProject(projectId);

        assertThat(project.getDeletedAt()).isNotNull();
        verify(projectRepository, never()).delete(any(Project.class));
    }

    @Test
    @DisplayName("프로젝트 삭제 실패 - 소유자가 아닌자가 삭제 시도")
    void deleteCompletedProject_NotOwner_Fail() {
        String loginId = "not_owner_user";
        Long projectId = 101L;

        Member owner = Member.builder().id(1L).loginId("actual_owner").build();
        Member stranger = Member.builder().id(2L).loginId(loginId).build();

        Project project = Project.builder()
                .id(projectId)
                .owner(owner)
                .status(ProjectStatus.COMPLETED)
                .build();

        given(SecurityUtil.getCurrentLoginId()).willReturn(loginId);
        given(memberRepository.findByLoginId(loginId)).willReturn(Optional.of(stranger));
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

        GeneralException exception = assertThrows(GeneralException.class, () ->
                myPageProjectService.deleteCompletedProject(projectId)
        );

        assertEquals(MyPageErrorCode.PROJECT_NOT_OWNER, exception.getCode());
        verify(projectRepository, never()).delete(any(Project.class));
    }

    @Test
    @DisplayName("프로젝트 삭제 실패 - 현재 진행중인 프로젝트 삭제 시도")
    void deleteCompletedProject_NotCompleted_Fail() {
        String loginId = "owner_user";
        Long projectId = 101L;

        Member owner = Member.builder().id(1L).loginId(loginId).build();
        Project project = Project.builder()
                .id(projectId)
                .owner(owner)
                .status(ProjectStatus.IN_PROGRESS)
                .build();

        given(SecurityUtil.getCurrentLoginId()).willReturn(loginId);
        given(memberRepository.findByLoginId(loginId)).willReturn(Optional.of(owner));
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));
        GeneralException exception = assertThrows(GeneralException.class, () ->
                myPageProjectService.deleteCompletedProject(projectId)
        );

        assertEquals(MyPageErrorCode.PROJECT_NOT_COMPLETED, exception.getCode());

        verify(projectRepository, never()).delete(any(Project.class));
    }


    @Test
    @DisplayName("프로젝트 삭제 실패 - 존재하지 않는 프로젝트 ID")
    void deleteCompletedProject_NotFound_Fail() {
        String loginId = "test_user";
        Long nonExistentProjectId = 999L;
        Member member = Member.builder().id(1L).loginId(loginId).build();

        given(SecurityUtil.getCurrentLoginId()).willReturn(loginId);
        given(memberRepository.findByLoginId(loginId)).willReturn(Optional.of(member));

        given(projectRepository.findById(nonExistentProjectId)).willReturn(Optional.empty());

        GeneralException exception = assertThrows(GeneralException.class, () ->
                myPageProjectService.deleteCompletedProject(nonExistentProjectId)
        );

        assertEquals(MyPageErrorCode.PROJECT_NOT_FOUND, exception.getCode());
        verify(projectRepository, never()).delete(any(Project.class));
    }
}
