package com.orchestration.metadata.dto;

import java.util.Set;
import java.util.UUID;

public record ResponseDocumentDto(UUID id, Set<String> tags, String title, String fileId,
                                  String originalFileName, String sha256) {
}
