package com.orchestration.search.dto;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Set;
import java.util.UUID;


public record CreateEsDot(String id, String title, List<String> tags, String fileId, String content) {
}
