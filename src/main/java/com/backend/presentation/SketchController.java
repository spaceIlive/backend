package com.backend.presentation;

import com.backend.application.ClaudeService;
import com.backend.dto.ImageAnalysisResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            
            // 🐛 디버깅: 실제 Claude 응답 로그 출력
            System.out.println("=== Claude API 응답 ===");
            System.out.println(analysisResult);
            System.out.println("=====================");
            
            // AI 응답을 파싱하여 구조화된 데이터로 변환
            return ResponseEntity.ok(parseAnalysisResult(analysisResult));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ImageAnalysisResponseDTO.failure("이미지 분석 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    // AI 응답 텍스트를 파싱하여 DTO로 변환
    private ImageAnalysisResponseDTO parseAnalysisResult(String analysisResult) {
        try {
            String guess = null;
            Integer confidence = null;
            List<String> otherPossibilities = new ArrayList<>();
            String reason = null;
            
            // 새로운 점수 필드들
            Integer drawingQualityScore = null;
            Integer creativityScore = null;
            Integer overallScore = null;
            String overallReason = null;

            // 각 항목을 정규식으로 추출
            String[] lines = analysisResult.split("\n");
            
            for (String line : lines) {
                line = line.trim();
                
                // 한국어 + 영어 버전 모두 지원
                if (line.startsWith("- 추측:") || line.startsWith("추측:") || 
                    line.startsWith("- Guess:") || line.startsWith("Guess:")) {
                    guess = line.replaceFirst("^-?\\s*(추측|Guess):\\s*", "").trim();
                }
                else if (line.startsWith("- 확신도:") || line.startsWith("확신도:") ||
                         line.startsWith("- Confidence:") || line.startsWith("Confidence:")) {
                    Pattern pattern = Pattern.compile("(\\d+)");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        confidence = Integer.parseInt(matcher.group(1));
                    }
                }
                else if (line.startsWith("- 다른 가능성:") || line.startsWith("다른 가능성:") ||
                         line.startsWith("- Other possibilities:") || line.startsWith("Other possibilities:")) {
                    String possibilities = line.replaceFirst("^-?\\s*(다른 가능성|Other possibilities):\\s*", "").trim();
                    if (!possibilities.isEmpty()) {
                        otherPossibilities = Arrays.asList(possibilities.split("[,，]"));
                        // 각 항목의 공백 제거
                        otherPossibilities = otherPossibilities.stream()
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .toList();
                    }
                }
                else if (line.startsWith("- 이유:") || line.startsWith("이유:") ||
                         line.startsWith("- Reason:") || line.startsWith("Reason:")) {
                    reason = line.replaceFirst("^-?\\s*(이유|Reason):\\s*", "").trim();
                }
                else if (line.startsWith("- 그림 품질 점수:") || line.startsWith("그림 품질 점수:")) {
                    Pattern pattern = Pattern.compile("(\\d+)");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        drawingQualityScore = Integer.parseInt(matcher.group(1));
                    }
                }
                else if (line.startsWith("- 창의성 점수:") || line.startsWith("창의성 점수:")) {
                    Pattern pattern = Pattern.compile("(\\d+)");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        creativityScore = Integer.parseInt(matcher.group(1));
                    }
                }
                else if (line.startsWith("- 전체 평가 점수:") || line.startsWith("전체 평가 점수:")) {
                    Pattern pattern = Pattern.compile("(\\d+)");
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        overallScore = Integer.parseInt(matcher.group(1));
                    }
                }
                else if (line.startsWith("- 종합 평가 이유:") || line.startsWith("종합 평가 이유:")) {
                    overallReason = line.replaceFirst("^-?\\s*종합 평가 이유:\\s*", "").trim();
                }
            }
            
            // 구조화된 형식이 아닌 경우 전체 텍스트를 힌트로 사용
            if (guess == null && analysisResult.length() > 0) {
                // 간단한 추출 시도
                if (analysisResult.toLowerCase().contains("cat") || analysisResult.contains("고양이")) {
                    guess = "고양이";
                    confidence = 70;
                } else if (analysisResult.toLowerCase().contains("dog") || analysisResult.contains("강아지")) {
                    guess = "강아지";
                    confidence = 70;
                } else {
                    guess = "그림";
                    confidence = 30;
                }
                reason = analysisResult.length() > 100 ? analysisResult.substring(0, 100) + "..." : analysisResult;
                
                // 점수 기본값 설정 (구조화되지 않은 응답의 경우)
                drawingQualityScore = 50;
                creativityScore = 50;
                overallScore = 50;
                overallReason = "구조화된 분석이 아닌 일반적인 응답입니다.";
            }

            // 파싱 실패 시 기본값 처리
            if (guess == null || guess.isEmpty()) {
                guess = "알 수 없음";
            }
            if (confidence == null) {
                confidence = 0;
            }
            if (reason == null || reason.isEmpty()) {
                reason = "분석 결과를 확인해주세요.";
            }
            
            // 점수 필드 기본값 처리
            if (drawingQualityScore == null) {
                drawingQualityScore = 0;
            }
            if (creativityScore == null) {
                creativityScore = 0;
            }
            if (overallScore == null) {
                overallScore = 0;
            }
            if (overallReason == null || overallReason.isEmpty()) {
                overallReason = "종합 평가를 확인해주세요.";
            }

            return ImageAnalysisResponseDTO.success(guess, confidence, otherPossibilities, reason,
                    drawingQualityScore, creativityScore, overallScore, overallReason);

        } catch (Exception e) {
            // 파싱 실패 시 기본 응답
            return ImageAnalysisResponseDTO.success("분석 실패", 0, new ArrayList<>(), 
                    "응답을 파싱할 수 없습니다: " + analysisResult, 0, 0, 0, "파싱 에러로 인한 평가 실패");
        }
    }
} 