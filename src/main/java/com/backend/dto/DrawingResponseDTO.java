package com.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrawingResponseDTO {
    private Long drawing_id;
    private String title;
    private String content;
    private Long folder_id;  // 엔티티 대신 ID만 포함
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 