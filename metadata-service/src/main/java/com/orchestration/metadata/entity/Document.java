package com.orchestration.metadata.entity;


import com.orchestration.metadata.constants.DocumentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="documents",
indexes = {@Index(name="idx_document_title", columnList = "title")},
uniqueConstraints = {@UniqueConstraint(name="uk_document_file_id",columnNames = {"file_id"})})
@Getter
@Setter
public class Document extends BaseEntity {


    @Column(name="fileid",nullable = false)
    private String fileId;

    @Column(name="sha256")
    private String sha256;

    @Enumerated(EnumType.STRING)
    @Column(name="status",nullable = false)
    private DocumentStatus status=DocumentStatus.ACTIVE;

    @Column(name="version",nullable = false)
    private int currentVersion=1;


    @Column(name="title")
    private String title;

    @Column(name="content_type")
    private String cotentType;

    @Column(name="size_byte")
    private long sizeByte;

    @Column(name="original_file_name")
    private String originalFileName;

    @ElementCollection
    @CollectionTable(   name="documents_tags", joinColumns = @JoinColumn(name="document_id"))
    @Column(name="tag", nullable = false)
    private Set<String> tags=new HashSet<>();


}
