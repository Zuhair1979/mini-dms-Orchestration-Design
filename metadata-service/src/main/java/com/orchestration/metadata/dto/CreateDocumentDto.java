package com.orchestration.metadata.dto;

import java.util.Set;

public record CreateDocumentDto(Set<String> tags, String title, String fileId) {
}
