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
        requestBody.put("max_tokens", 1000);
        
        // 메시지 구성
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        
        // 컨텐츠 구성 (텍스트 + 이미지)
        Map<String, Object> textContent = new HashMap<>();
        textContent.put("type", "text");
        textContent.put("text", "이 그림을 분석해서 어떤 유명한 그림이나 작품과 닮았는지 알려주세요. 답변은 간단하게 작품명만 말해주세요.");
        
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