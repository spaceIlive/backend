package com.backend.presentation;

import com.backend.application.DrawingService;
import com.backend.dto.DrawingResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drawings")
public class DrawingController {

    private final DrawingService drawingService;

    public DrawingController(DrawingService drawingService) {
        this.drawingService = drawingService;
    }

    // 새로운 스케치 생성 및 저장
    @PostMapping
    public ResponseEntity<DrawingResponseDTO> createDrawing(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Long folderId
    ) {
        try {
            DrawingResponseDTO saved = drawingService.createDrawing(title, content, folderId);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 요청 본문을 위한 record
    private record DrawingUpdateRequest(String title, String content) {}

    // 스케치 수정
    @PutMapping("/{drawing_id}")
    public ResponseEntity<DrawingResponseDTO> updateDrawing(
            @PathVariable Long drawing_id,
            @RequestBody DrawingUpdateRequest request
    ) {
        try {
            DrawingResponseDTO updated = drawingService.updateDrawing(drawing_id, request.title(), request.content());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 폴더 내 모든 스케치 조회
    @GetMapping("/folder/{folderId}")
    public ResponseEntity<List<DrawingResponseDTO>> getDrawingsByFolder(@PathVariable Long folderId) {
        try {
            List<DrawingResponseDTO> drawings = drawingService.getDrawingsByFolder(folderId);
            return ResponseEntity.ok(drawings);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 스케치 삭제
    @DeleteMapping("/{drawing_id}")
    public ResponseEntity<Void> deleteDrawing(@PathVariable Long drawing_id) {
        try {
            drawingService.deleteDrawing(drawing_id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
