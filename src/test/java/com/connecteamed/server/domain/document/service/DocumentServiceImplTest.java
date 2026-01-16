package com.connecteamed.server.domain.document.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.connecteamed.server.domain.document.dto.DocumentCreateRes;
import com.connecteamed.server.domain.document.dto.DocumentCreateTextReq;
import com.connecteamed.server.domain.document.dto.DocumentUploadRes;
import com.connecteamed.server.domain.document.entity.Document;
import com.connecteamed.server.domain.document.enums.DocumentFileType;
import com.connecteamed.server.domain.document.repository.DocumentRepository;
import com.connecteamed.server.domain.project.entity.Project;
import com.connecteamed.server.domain.project.entity.ProjectMember;
import com.connecteamed.server.domain.project.repository.ProjectMemberRepository;
import com.connecteamed.server.domain.project.repository.ProjectRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DocumentServiceImplTest {

    // MockitoExtension이 만들긴 하는데, setUp에서 커스텀 mock으로 갈아끼울 겁니다.
    @Mock ProjectRepository projectRepository;
    @Mock ProjectMemberRepository projectMemberRepository;
    @Mock DocumentRepository documentRepository;
    @Mock S3StorageService s3StorageService; // 실제 타입명 맞추세요

    @InjectMocks DocumentServiceImpl documentService; // 실제 클래스명 맞추세요

    private Project projectRef;
    private ProjectMember memberRef;

    @BeforeEach
    void setUp() {
        Long projectId = 1L;

        projectRef = mock(Project.class);
        memberRef = mock(ProjectMember.class, RETURNS_DEEP_STUBS);

        // 서비스가 "멤버가 이 프로젝트 소속인지" 체크할 때 쓰는 체인 대비
        // memberRef.getProject().getId() 같은 걸 호출해도 1L로 나오게
        try {
            when(projectRef.getId()).thenReturn(projectId);
        } catch (Throwable ignore) {}

        try {
            when(memberRef.getProject().getId()).thenReturn(projectId);
        } catch (Throwable ignore) {}

        // 1) projectRepository: 어떤 메서드를 호출하든 Project/Optional<Project>/boolean을 적당히 반환
        ProjectRepository projectRepoAny = mock(ProjectRepository.class, invocation -> {
            Class<?> rt = invocation.getMethod().getReturnType();

            if (rt == Project.class) return projectRef;
            if (rt == Optional.class) return Optional.of(projectRef);
            if (rt == boolean.class || rt == Boolean.class) return true;

            // void거나 그 외는 기본값
            return Answers.RETURNS_DEFAULTS.answer(invocation);
        });

        // 2) projectMemberRepository: 어떤 메서드를 호출하든 ProjectMember/Optional<ProjectMember>/boolean을 반환
        ProjectMemberRepository memberRepoAny = mock(ProjectMemberRepository.class, invocation -> {
            Class<?> rt = invocation.getMethod().getReturnType();

            if (rt == ProjectMember.class) return memberRef;
            if (rt == Optional.class) return Optional.of(memberRef);
            if (rt == boolean.class || rt == Boolean.class) return true;

            return Answers.RETURNS_DEFAULTS.answer(invocation);
        });

        // documentService 내부 필드에 갈아끼우기
        ReflectionTestUtils.setField(documentService, "projectRepository", projectRepoAny);
        ReflectionTestUtils.setField(documentService, "projectMemberRepository", memberRepoAny);

        // 테스트에서도 같은 mock을 참조하도록 교체
        this.projectRepository = projectRepoAny;
        this.projectMemberRepository = memberRepoAny;

        // 저장 시 id/createdAt 세팅
        given(documentRepository.save(any(Document.class))).willAnswer(invocation -> {
            Document d = invocation.getArgument(0);
            ReflectionTestUtils.setField(d, "id", 1L);
            ReflectionTestUtils.setField(d, "createdAt", java.time.Instant.now());
            return d;
        });

        // S3 업로드는 어떤 prefix가 와도 통과
        given(s3StorageService.upload(any(MultipartFile.class), anyString()))
                .willReturn("documents/project-1/uuid_image.png");
    }

    @Test
    @DisplayName("텍스트 문서 생성: 프로젝트/멤버 참조 후 저장하고 응답을 반환한다")
    void createText_success() {
        Long projectId = 1L;
        Long projectMemberId = 1L;
        var req = new DocumentCreateTextReq("제목", "내용");

        DocumentCreateRes res = documentService.createText(projectId, projectMemberId, req);

        assertThat(res).isNotNull();

        // 저장된 엔티티 값 검증
        var captor = org.mockito.ArgumentCaptor.forClass(Document.class);
        then(documentRepository).should().save(captor.capture());

        Document saved = captor.getValue();
        assertThat(saved.getFileType()).isEqualTo(DocumentFileType.TEXT);
        assertThat(saved.getTitle()).isEqualTo("제목");
        assertThat(saved.getContent()).isEqualTo("내용");
        assertThat(saved.getFileUrl()).isNull();
    }

    @Test
    @DisplayName("파일 업로드: type이 TEXT면 예외")
    void uploadFile_rejectTextType() {
        MultipartFile file = new MockMultipartFile(
                "file", "a.txt", "text/plain", "hello".getBytes()
        );

        assertThatThrownBy(() -> documentService.uploadFile(1L, 1L, file, DocumentFileType.TEXT))
                .isInstanceOf(RuntimeException.class);

        then(s3StorageService).shouldHaveNoInteractions();
        then(documentRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("파일 업로드: S3 업로드 후 Document 저장하고 응답 반환")
    void uploadFile_success() {
        Long projectId = 1L;
        Long projectMemberId = 1L;

        MultipartFile file = new MockMultipartFile(
                "file",
                "image.png",
                "image/png",
                new byte[] {1,2,3}
        );

        DocumentUploadRes res = documentService.uploadFile(projectId, projectMemberId, file, DocumentFileType.IMAGE);

        assertThat(res).isNotNull();
        assertThat(res.fileName()).isEqualTo("image.png");

        then(s3StorageService).should().upload(eq(file), anyString());
        then(documentRepository).should().save(any(Document.class));
    }

    @Test
    @DisplayName("다운로드: TEXT 문서는 다운로드 대상이 아니므로 예외")
    void download_rejectTextDocument() {
        Document textDoc = mock(Document.class);
        given(textDoc.getFileType()).willReturn(DocumentFileType.TEXT);

        given(documentRepository.findByIdAndDeletedAtIsNull(10L))
                .willReturn(Optional.of(textDoc));

        assertThatThrownBy(() -> documentService.download(10L))
                .isInstanceOf(RuntimeException.class);
    }
}
