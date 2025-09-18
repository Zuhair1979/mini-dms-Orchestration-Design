package com.orchestration.metadata.dto;

import java.util.List;
import java.util.Set;
import java.util.UUID;


public record CreateEsDot(UUID id, String title, Set<String> tags, String fileId, String content) {
}
