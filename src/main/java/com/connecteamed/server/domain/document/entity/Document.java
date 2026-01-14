package com.connecteamed.server.domain.document.entity;

import com.connecteamed.server.domain.document.enums.FileType;
import com.connecteamed.server.domain.project.entity.Project;
import com.connecteamed.server.domain.project.entity.ProjectMember;
import com.connecteamed.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;


@Entity
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@Getter
@Table(name= "document")
public class Document extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="public_id",nullable = false,unique = true,columnDefinition = "UUID")
    private UUID publicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id",nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_member_id",nullable = false)
    private ProjectMember projectMember;

    @Column(name="title",nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name="file_type",nullable = false)
    private FileType fileType;

    @Column(name = "file_url", columnDefinition = "TEXT")
    private String fileUrl;

    @Column(name="content", columnDefinition = "TEXT")
    private String content;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    public void prePersist(){
        if(this.publicId == null){
            this.publicId = UUID.randomUUID();
        }
    }

}
