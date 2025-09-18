package com.orchestration.metadata.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    // this is the internal number of the table document

@Id
@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;

@CreationTimestamp
@Column(name="created_at")
private Instant createdAt;


}
