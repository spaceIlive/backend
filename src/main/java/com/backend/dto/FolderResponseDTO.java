package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FolderResponseDTO {
    private Long folder_id;
    private String name;
    private Long parent_id;  // 엔티티 대신 ID만 포함
    private Long user_id;    // 엔티티 대신 ID만 포함
    private List<FolderResponseDTO> children;  // 재귀적 구조 유지
    private List<DrawingResponseDTO> drawings; // Drawing도 DTO로 변환
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 