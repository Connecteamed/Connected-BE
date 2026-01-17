package com.connecteamed.server.domain.document.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.connecteamed.server.domain.document.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Optional<Document> findByIdAndDeletedAtIsNull(Long id);

    List<Document> findAllByProjectIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long projectId);
}
