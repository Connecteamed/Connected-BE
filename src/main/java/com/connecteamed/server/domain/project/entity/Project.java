package com.connecteamed.server.domain.project.entity;


import com.connecteamed.server.domain.member.entity.Member;
import com.connecteamed.server.domain.project.enums.ProjectStatus;
import com.connecteamed.server.domain.task.enums.TaskStatus;
import com.connecteamed.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="owner_id",nullable = false)
    private Member owner;

    @Column(name="name",nullable = false)
    private String name;

    @Column(name="goal",nullable = false, columnDefinition = "TEXT")
    private String goal;

    @Enumerated(EnumType.STRING)
    @Column(name="status",nullable = false)
    @Builder.Default
    private ProjectStatus status=ProjectStatus.IN_PROGRESS;

    @PrePersist
    public void prePersist(){
        if(this.publicId == null){
            this.publicId = UUID.randomUUID();
        }
    }


}
