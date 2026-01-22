package com.connecteamed.server.domain.retrospective.entity;


import com.connecteamed.server.domain.project.entity.Project;
import com.connecteamed.server.domain.project.entity.ProjectMember;
import com.connecteamed.server.domain.task.entity.Task;
import com.connecteamed.server.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@NoArgsConstructor(access= AccessLevel.PROTECTED)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
@Getter
@Table(name = "ai_retrospective")
@SQLDelete(sql = "UPDATE ai_retrospective SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class AiRetrospective extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
/*
    @Column(name = "public_id", nullable = false, unique = true)
    private Long publicId;

 */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private ProjectMember writer;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "project_result", nullable = false, columnDefinition = "TEXT")
    private String projectResult;

    @Builder.Default
    @OneToMany(mappedBy = "aiRetrospective", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RetrospectiveTask> retrospectiveTasks = new ArrayList<>();

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public void addRetrospectiveTask(Task task) {
        RetrospectiveTask retrospectiveTask = RetrospectiveTask.builder()
                .aiRetrospective(this)
                .task(task)
                .build();
        this.retrospectiveTasks.add(retrospectiveTask);
    }

    public void update(String title, String projectResult) {
        this.title = title;
        this.projectResult = projectResult;
    }

    public void updateDeletedAt(Instant now) {
        this.deletedAt = now;
    }

    /*
    @PrePersist
    public void prePersist() {
        if (this.publicId == null) {
            this.publicId = this.id;
        }
    }

     */
}

