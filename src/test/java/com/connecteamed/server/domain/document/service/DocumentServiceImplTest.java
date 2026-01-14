package com.connecteamed.server.domain.document.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
class DocumentServiceImplTest {

    @Mock ProjectRepository projectRepository;
    @Mock ProjectMemberRepository projectMemberRepository;
    @Mock DocumentRepository documentRepository;
    @Mock S3StorageService s3StorageService; // 인터페이스/구현명 맞추세요

    @InjectMocks DocumentServiceImpl documentService; // 실제 서비스 구현 클래스명 맞추세요

    @Test
    @DisplayName("텍스트 문서 생성: 프로젝트/멤버 참조 후 저장하고 응답을 반환한다")
    void createText_success() {
        // given
        Long projectId = 1L;
        Long projectMemberId = 1L;
        var req = new DocumentCreateTextReq("제목", "내용");

        Project projectRef = mock(Project.class);
        ProjectMember memberRef = mock(ProjectMember.class);

        given(projectRepository.getReferenceById(projectId)).willReturn(projectRef);
        given(projectMemberRepository.getReferenceById(projectMemberId)).willReturn(memberRef);

        // save될 Document의 id/createdAt이 필요하므로, save 시점에 값을 세팅해주는 방식으로 처리
        given(documentRepository.save(any(Document.class))).willAnswer(invocation -> {
            Document d = invocation.getArgument(0);
            // 테스트용으로 리플렉션 대신, Document 엔티티가 id/createdAt getter를 제대로 반환한다고 가정
            ReflectionTestUtils.setField(d, "id", 1L);
            ReflectionTestUtils.setField(d, "createdAt", OffsetDateTime.now());
            return d;
        });

        // when
        DocumentCreateRes res = documentService.createText(projectId, projectMemberId, req);

        // then
        // save가 호출됐는지 + 저장된 Document의 필드가 기대대로인지 확인
        ArgumentCaptor<Document> captor = ArgumentCaptor.forClass(Document.class);
        then(documentRepository).should().save(captor.capture());

        Document saved = captor.getValue();
        assertThat(saved.getFileType()).isEqualTo(DocumentFileType.TEXT);
        assertThat(saved.getTitle()).isEqualTo("제목");
        assertThat(saved.getContent()).isEqualTo("내용");
        assertThat(saved.getFileUrl()).isNull();

        // 응답 검증(프로젝트 상황에 따라 createdAt이 null일 수 있음)
        assertThat(res).isNotNull();
    }

    @Test
    @DisplayName("파일 업로드: type이 TEXT면 예외")
    void uploadFile_rejectTextType() {
        // given
        MultipartFile file = new MockMultipartFile(
                "file", "a.txt", "text/plain", "hello".getBytes()
        );

        // when + then
        assertThatThrownBy(() -> documentService.uploadFile(1L, 1L, file, DocumentFileType.TEXT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TEXT는 파일 업로드 타입이 아닙니다");

        then(s3StorageService).shouldHaveNoInteractions();
        then(documentRepository).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("파일 업로드: S3 업로드 후 Document 저장하고 응답 반환")
    void uploadFile_success() {
        // given
        Long projectId = 1L;
        Long projectMemberId = 1L;

        MultipartFile file = new MockMultipartFile(
                "file",
                "image.png",
                "image/png",
                new byte[] {1,2,3}
        );

        Project projectRef = mock(Project.class);
        ProjectMember memberRef = mock(ProjectMember.class);

        given(projectRepository.getReferenceById(projectId)).willReturn(projectRef);
        given(projectMemberRepository.getReferenceById(projectMemberId)).willReturn(memberRef);

        // 여기서 s3StorageService.upload가 key를 반환하도록
        given(s3StorageService.upload(eq(file), eq("project-" + projectId)))
                .willReturn("documents/project-1/uuid_image.png");

        // documentRepository.save가 저장된 엔티티를 반환하도록(필요하면 id/createdAt 가정)
        given(documentRepository.save(any(Document.class))).willAnswer(invocation -> {
            Document d = invocation.getArgument(0);

            ReflectionTestUtils.setField(d, "id", 1L);
            ReflectionTestUtils.setField(d, "createdAt", OffsetDateTime.now());

            return d;
        
        });

        // when
        DocumentUploadRes res = documentService.uploadFile(projectId, projectMemberId, file, DocumentFileType.IMAGE);

        // then
        then(s3StorageService).should().upload(eq(file), eq("project-" + projectId));
        then(documentRepository).should().save(any(Document.class));

        assertThat(res).isNotNull();
        assertThat(res.fileName()).isEqualTo("image.png");
    }

    @Test
    @DisplayName("다운로드: TEXT 문서는 다운로드 대상이 아니므로 예외")
    void download_rejectTextDocument() {
        // given
        Document textDoc = mock(Document.class);
        given(textDoc.getFileType()).willReturn(DocumentFileType.TEXT);

        given(documentRepository.findByIdAndDeletedAtIsNull(10L))
                .willReturn(Optional.of(textDoc));

        // when + then
        assertThatThrownBy(() -> documentService.download(10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TEXT 문서는 다운로드 대상이 아닙니다.");
    }
}
