package com.connecteamed.server.domain.project.service;

import com.connecteamed.server.domain.member.entity.Member;
import com.connecteamed.server.domain.member.enums.SocialType;
import com.connecteamed.server.domain.member.repository.MemberRepository;
import com.connecteamed.server.domain.project.code.ProjectErrorCode;
import com.connecteamed.server.domain.project.dto.ProjectCreateReq;
import com.connecteamed.server.domain.project.dto.ProjectListRes;
import com.connecteamed.server.domain.project.dto.ProjectRes;
import com.connecteamed.server.domain.project.dto.ProjectUpdateReq;
import com.connecteamed.server.domain.project.entity.Project;
import com.connecteamed.server.domain.project.entity.ProjectMember;
import com.connecteamed.server.domain.project.entity.ProjectRequiredRole;
import com.connecteamed.server.domain.project.entity.ProjectRole;
import com.connecteamed.server.domain.project.enums.ProjectStatus;
import com.connecteamed.server.domain.project.repository.ProjectMemberRepository;
import com.connecteamed.server.domain.project.repository.ProjectRepository;
import com.connecteamed.server.domain.project.repository.ProjectRequiredRoleRepository;
import com.connecteamed.server.domain.project.repository.ProjectRoleRepository;
import com.connecteamed.server.global.apiPayload.exception.GeneralException;
import com.connecteamed.server.global.util.S3Uploader;
import com.connecteamed.server.global.util.SecurityUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService 테스트")
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectRoleRepository projectRoleRepository;

    @Mock
    private ProjectRequiredRoleRepository projectRequiredRoleRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private S3Uploader s3Uploader;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    private static MockedStatic<SecurityUtil> mockedSecurityUtil;

    @InjectMocks
    private ProjectService projectService;

    private Member testMember;
    private Project testProject;
    private ProjectRole designerRole;
    private ProjectRole serverRole;
    private List<String> requiredRoleNames;

    @BeforeAll
    static void setup() {
        mockedSecurityUtil = mockStatic(SecurityUtil.class);
    }

    @AfterAll
    static void tearDown() {
        mockedSecurityUtil.close();
    }

    @BeforeEach
    void setUp() {
        // 테스트용 멤버 생성
        testMember = Member.builder()
                .id(1L)
                .publicId(UUID.randomUUID())
                .name("테스트사용자")
                .loginId("test@example.com")
                .socialType(SocialType.LOCAL)
                .build();

        // 테스트용 프로젝트 생성
        testProject = Project.builder()
                .id(1L)
                .publicId(UUID.randomUUID())
                .owner(testMember)
                .name("UMC 7기")
                .goal("앱 런칭")
                .imageUrl(null)
                .build();
        ReflectionTestUtils.setField(testProject, "createdAt", Instant.now());
        ReflectionTestUtils.setField(testProject, "updatedAt", Instant.now());

        // 테스트용 역할 생성
        designerRole = ProjectRole.builder()
                .id(1L)
                .roleName("DESIGNER")
                .build();

        serverRole = ProjectRole.builder()
                .id(2L)
                .roleName("SERVER")
                .build();

        requiredRoleNames = Arrays.asList("DESIGNER", "SERVER", "ANDROID");
    }

    @Test
    @DisplayName("프로젝트 생성 성공")
    void createProject_Success() {
        // given
        ProjectCreateReq createReq = ProjectCreateReq.builder()
                .name("UMC 7기")
                .goal("앱 런칭")
                .requiredRoleNames(requiredRoleNames)
                .build();

        when(projectRepository.findByName("UMC 7기")).thenReturn(Optional.empty());
        when(memberRepository.findByLoginId("test@example.com")).thenReturn(Optional.of(testMember));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        when(projectRoleRepository.findByRoleName("DESIGNER")).thenReturn(Optional.of(designerRole));
        when(projectRoleRepository.findByRoleName("SERVER")).thenReturn(Optional.of(serverRole));
        when(projectRoleRepository.findByRoleName("ANDROID")).thenReturn(Optional.of(
                ProjectRole.builder().id(3L).roleName("ANDROID").build()
        ));

        // when
        ProjectRes.CreateResponse response = projectService.createProject(createReq, "test@example.com");

        // then
        assertNotNull(response);
        assertEquals(1L, response.getProjectId());
        assertNotNull(response.getCreatedAt());
        verify(projectRepository, times(1)).save(any(Project.class));
        verify(projectRequiredRoleRepository, times(3)).save(any(ProjectRequiredRole.class));
    }

    @Test
    @DisplayName("프로젝트 생성 실패 - 프로젝트명 중복")
    void createProject_Fail_DuplicateName() {
        // given
        ProjectCreateReq createReq = ProjectCreateReq.builder()
                .name("UMC 7기")
                .goal("앱 런칭")
                .requiredRoleNames(requiredRoleNames)
                .build();

        when(projectRepository.findByName("UMC 7기")).thenReturn(Optional.of(testProject));

        // when & then
        GeneralException exception = assertThrows(GeneralException.class, () ->
                projectService.createProject(createReq, "test@example.com")
        );
        assertEquals(ProjectErrorCode.PROJECT_NAME_ALREADY_EXISTS, exception.getCode());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("프로젝트 생성 실패 - 회원 없음")
    void createProject_Fail_MemberNotFound() {
        // given
        ProjectCreateReq createReq = ProjectCreateReq.builder()
                .name("UMC 7기")
                .goal("앱 런칭")
                .requiredRoleNames(requiredRoleNames)
                .build();

        when(projectRepository.findByName("UMC 7기")).thenReturn(Optional.empty());
        when(memberRepository.findByLoginId("test@example.com")).thenReturn(Optional.empty());

        // when & then
        GeneralException exception = assertThrows(GeneralException.class, () ->
                projectService.createProject(createReq, "test@example.com")
        );
        assertEquals(ProjectErrorCode.MEMBER_NOT_FOUND, exception.getCode());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("프로젝트 생성 실패 - 역할 없음")
    void createProject_Fail_RoleNotFound() {
        // given
        ProjectCreateReq createReq = ProjectCreateReq.builder()
                .name("UMC 7기")
                .goal("앱 런칭")
                .requiredRoleNames(Arrays.asList("INVALID_ROLE"))
                .build();

        when(projectRepository.findByName("UMC 7기")).thenReturn(Optional.empty());
        when(memberRepository.findByLoginId("test@example.com")).thenReturn(Optional.of(testMember));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);
        when(projectRoleRepository.findByRoleName("INVALID_ROLE")).thenReturn(Optional.empty());

        // when & then
        GeneralException exception = assertThrows(GeneralException.class, () ->
                projectService.createProject(createReq, "test@example.com")
        );
        assertEquals(ProjectErrorCode.ROLE_NOT_FOUND, exception.getCode());
    }

    @Test
    @DisplayName("프로젝트 상세 조회 성공")
    void getProjectDetail_Success() {
        // given
        ProjectRequiredRole requiredRole1 = ProjectRequiredRole.builder()
                .id(1L)
                .project(testProject)
                .projectRole(designerRole)
                .build();

        ProjectRequiredRole requiredRole2 = ProjectRequiredRole.builder()
                .id(2L)
                .project(testProject)
                .projectRole(serverRole)
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectRequiredRoleRepository.findByProjectId(1L))
                .thenReturn(Arrays.asList(requiredRole1, requiredRole2));

        // when
        ProjectRes.DetailResponse response = projectService.getProjectDetail(1L);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getProjectId());
        assertEquals("UMC 7기", response.getName());
        assertEquals("앱 런칭", response.getGoal());
        assertEquals(2, response.getRequiredRoleNames().size());
        assertTrue(response.getRequiredRoleNames().contains("DESIGNER"));
        assertTrue(response.getRequiredRoleNames().contains("SERVER"));
    }

    @Test
    @DisplayName("프로젝트 상세 조회 실패 - 프로젝트 없음")
    void getProjectDetail_Fail_ProjectNotFound() {
        // given
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        GeneralException exception = assertThrows(GeneralException.class, () ->
                projectService.getProjectDetail(1L)
        );
        assertEquals(ProjectErrorCode.PROJECT_NOT_FOUND, exception.getCode());
    }

    @Test
    @DisplayName("프로젝트 수정 성공")
    void updateProject_Success() {
        // given
        ProjectUpdateReq updateReq = ProjectUpdateReq.builder()
                .name("UMC 8기")
                .goal("서비스 런칭")
                .requiredRoleNames(Arrays.asList("DESIGNER", "SERVER"))
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.findByName("UMC 8기")).thenReturn(Optional.empty());
        when(projectRequiredRoleRepository.findByProjectId(1L)).thenReturn(new ArrayList<>());
        when(projectRoleRepository.findByRoleName("DESIGNER")).thenReturn(Optional.of(designerRole));
        when(projectRoleRepository.findByRoleName("SERVER")).thenReturn(Optional.of(serverRole));

        // when
        ProjectRes.CreateResponse response = projectService.updateProject(1L, updateReq);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getProjectId());
        verify(projectRequiredRoleRepository, times(1)).deleteAll(any());
        verify(projectRequiredRoleRepository, times(2)).save(any(ProjectRequiredRole.class));
    }

    @Test
    @DisplayName("프로젝트 수정 실패 - 프로젝트 없음")
    void updateProject_Fail_ProjectNotFound() {
        // given
        ProjectUpdateReq updateReq = ProjectUpdateReq.builder()
                .name("UMC 8기")
                .goal("서비스 런칭")
                .requiredRoleNames(Arrays.asList("DESIGNER"))
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        GeneralException exception = assertThrows(GeneralException.class, () ->
                projectService.updateProject(1L, updateReq)
        );
        assertEquals(ProjectErrorCode.PROJECT_NOT_FOUND, exception.getCode());
    }

    @Test
    @DisplayName("프로젝트 수정 실패 - 프로젝트명 중복")
    void updateProject_Fail_DuplicateName() {
        // given
        Project anotherProject = Project.builder()
                .id(2L)
                .publicId(UUID.randomUUID())
                .owner(testMember)
                .name("UMC 8기")
                .goal("다른 목표")
                .build();

        ProjectUpdateReq updateReq = ProjectUpdateReq.builder()
                .name("UMC 8기")
                .goal("서비스 런칭")
                .requiredRoleNames(Arrays.asList("DESIGNER"))
                .build();

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        when(projectRepository.findByName("UMC 8기")).thenReturn(Optional.of(anotherProject));

        // when & then
        GeneralException exception = assertThrows(GeneralException.class, () ->
                projectService.updateProject(1L, updateReq)
        );
        assertEquals(ProjectErrorCode.PROJECT_NAME_ALREADY_EXISTS, exception.getCode());
    }

    @Test
    @DisplayName("프로젝트 종료 성공")
    void closeProject_Success() {
        // given
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        // when
        ProjectRes.CloseResponse response = projectService.closeProject(1L);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getProjectId());
        assertEquals(ProjectStatus.COMPLETED, response.getStatus());
        assertNotNull(response.getClosedAt());
    }

    @Test
    @DisplayName("프로젝트 종료 실패 - 프로젝트 없음")
    void closeProject_Fail_ProjectNotFound() {
        // given
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        GeneralException exception = assertThrows(GeneralException.class, () ->
                projectService.closeProject(1L)
        );
        assertEquals(ProjectErrorCode.PROJECT_NOT_FOUND, exception.getCode());
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
        ProjectListRes.CompletedProjectList result = projectService.getMyCompletedProjects();

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
                .owner(owner) // 내가 주인임
                .status(ProjectStatus.COMPLETED) // 완료된 상태
                .build();

        // Mock 설정
        given(SecurityUtil.getCurrentLoginId()).willReturn(loginId);
        given(memberRepository.findByLoginId(loginId)).willReturn(Optional.of(owner));
        given(projectRepository.findById(projectId)).willReturn(Optional.of(project));

        // 2. 실행 (When)
        projectService.deleteCompletedProject(projectId);

        // 3. 검증 (Then)
        // 실제로 repository의 delete 메서드가 해당 프로젝트 객체로 호출되었는지 확인
        verify(projectRepository, times(1)).delete(project);
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
                projectService.deleteCompletedProject(projectId)
        );

        assertEquals(ProjectErrorCode.PROJECT_NOT_OWNER, exception.getCode());
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
                projectService.deleteCompletedProject(projectId)
        );

        assertEquals(ProjectErrorCode.PROJECT_NOT_COMPLETED, exception.getCode());

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
                projectService.deleteCompletedProject(nonExistentProjectId)
        );

        assertEquals(ProjectErrorCode.PROJECT_NOT_FOUND, exception.getCode());
        verify(projectRepository, never()).delete(any(Project.class));
    }
}

