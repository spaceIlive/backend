package com.backend.presentation;

import com.backend.application.ClaudeService;
import com.backend.dto.ImageAnalysisResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/sketch")
public class SketchController {

    private final ClaudeService claudeService;

    public SketchController(ClaudeService claudeService) {
        this.claudeService = claudeService;
    }

    // 이미지 분석 (POST /api/sketch/analyze)
    @PostMapping("/analyze")
    public ResponseEntity<ImageAnalysisResponseDTO> analyzeImage(
            @RequestParam("image") MultipartFile imageFile) {
        
        try {
            // 파일 유효성 검사
            if (imageFile.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ImageAnalysisResponseDTO.failure("이미지 파일이 비어있습니다."));
            }

            // PNG 파일 검사
            String contentType = imageFile.getContentType();
            if (contentType == null || !contentType.equals("image/png")) {
                return ResponseEntity.badRequest()
                        .body(ImageAnalysisResponseDTO.failure("PNG 파일만 업로드 가능합니다."));
            }

            // 파일 크기 제한 (10MB)
            if (imageFile.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                        .body(ImageAnalysisResponseDTO.failure("파일 크기는 10MB를 초과할 수 없습니다."));
            }

            // Claude API를 통한 이미지 분석
            String analysisResult = claudeService.analyzeImage(imageFile);
            
            return ResponseEntity.ok(ImageAnalysisResponseDTO.success(analysisResult));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ImageAnalysisResponseDTO.failure("이미지 분석 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
} 