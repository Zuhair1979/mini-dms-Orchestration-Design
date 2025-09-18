package com.orchestration.metadata.dao;

import com.orchestration.metadata.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    
}
