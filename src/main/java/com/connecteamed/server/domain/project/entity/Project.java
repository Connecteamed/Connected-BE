package com.connecteamed.server.domain.project.entity;


import com.connecteamed.server.domain.member.entity.Member;
import com.connecteamed.server.domain.project.enums.ProjectStatus;
import com.connecteamed.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@Getter
@Table(name= "project")
public class Project extends BaseEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="public_id",nullable = false,unique = true,columnDefinition = "UUID")
    private UUID publicId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="owner_id",nullable = false)
    private Member owner;

    @Column(name="name",nullable = false, unique = true)
    private String name;

    @Column(name="goal",nullable = false, columnDefinition = "TEXT")
    private String goal;

    @Column(name="image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    @Builder.Default
    private ProjectStatus status = ProjectStatus.IN_PROGRESS;

    @Column(name="closed_at")
    private Instant closedAt;

    @PrePersist
    public void prePersist(){
        if(this.publicId == null){
            this.publicId = UUID.randomUUID();
        }
    }

    // 비즈니스 로직: 프로젝트 정보 수정
    public void updateProject(String name, String goal) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (goal != null && !goal.isBlank()) {
            this.goal = goal;
        }
    }

    // 비즈니스 로직: 프로젝트 종료
    public void closeProject() {
        this.status = ProjectStatus.COMPLETED;
        this.closedAt = Instant.now();
    }
}
