package com.backend.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClaudeService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${claude.api.url}")
    private String apiUrl;

    @Value("${claude.api.key}")
    private String apiKey;

    @Value("${claude.api.model}")
    private String model;

    public ClaudeService() {
        this.webClient = WebClient.builder().build();
        this.objectMapper = new ObjectMapper();
    }

    public String analyzeImage(MultipartFile imageFile) {
        try {
            // 이미지를 Base64로 인코딩
            byte[] imageBytes = imageFile.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            // Claude API 요청 본문 구성
            Map<String, Object> requestBody = createRequestBody(base64Image, imageFile.getContentType());
            
            // API 호출
            String response = webClient.post()
                    .uri(apiUrl)
                    .header("Content-Type", "application/json")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", "2023-06-01")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 응답에서 결과 텍스트 추출
            return extractResultFromResponse(response);

        } catch (Exception e) {
            throw new RuntimeException("이미지 분석 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> createRequestBody(String base64Image, String contentType) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("max_tokens", 2000);  // 더 자세한 분석을 위해 토큰 수 증가
        requestBody.put("temperature", 0.3);  // 창의성보다 정확성을 위해 낮은 온도 설정
        
        // 메시지 구성
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        
        // 컨텐츠 구성 (텍스트 + 이미지)
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", createAnalysisPrompt());
        
        Map<String, Object> imageContent = new HashMap<>();
        imageContent.put("type", "image");
        
        Map<String, Object> imageSource = new HashMap<>();
        imageSource.put("type", "base64");
        imageSource.put("media_type", contentType);
        imageSource.put("data", base64Image);
        
        imageContent.put("source", imageSource);
        
        message.put("content", List.of(textContent, imageContent));
        requestBody.put("messages", List.of(message));
        
        return requestBody;
    }

    private String createAnalysisPrompt() {
        return """
        **중요: 반드시 아래 형식으로만 응답하세요! 다른 말은 하지 마세요!**
        
        이 이미지에서 보이는 것이 무엇인지 맞춰주세요.
        스케치든 사진이든 상관없이, 무엇을 나타내는지 추측해주세요.
        
        **형식을 정확히 지켜서 응답하세요:**
        
        - 추측: [답]
        - 확신도: [숫자]%
        - 다른 가능성: [답1, 답2, 답3]
        - 이유: [간단한 설명]
        
        **예시:**
        - 추측: 사과
        - 확신도: 90%
        - 다른 가능성: 토마토, 체리
        - 이유: 빨간색 둥근 과일 모양입니다
        
        **절대 다른 형식으로 답변하지 마세요. 오직 위 형식만 사용하세요.**
        """;
    }

    private String extractResultFromResponse(String response) {
        try {
            JsonNode jsonNode = objectMapper.readTree(response);
            JsonNode contentArray = jsonNode.get("content");
            
            if (contentArray != null && contentArray.isArray() && contentArray.size() > 0) {
                JsonNode firstContent = contentArray.get(0);
                JsonNode textNode = firstContent.get("text");
                
                if (textNode != null) {
                    return textNode.asText().trim();
                }
            }
            
            throw new RuntimeException("Claude API 응답에서 텍스트를 찾을 수 없습니다.");
            
        } catch (Exception e) {
            throw new RuntimeException("Claude API 응답 파싱 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
} 