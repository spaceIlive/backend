package com.backend.dto;

import java.util.List;

public class ImageAnalysisResponseDTO {
    private boolean success;
    private String message;
    
    // 스케치 맞추기 게임 필드들
    private String guess;           // 추측 (가장 가능성 높은 답)
    private Integer confidence;     // 확신도 (1-100%)
    private List<String> otherPossibilities;  // 다른 가능성들
    private String reason;          // 이유 (설명)
    
    // 그림 평가 점수 필드들
    private Integer drawingQualityScore;    // 그림 품질 점수 (0-100점)
    private Integer creativityScore;        // 창의성 점수 (0-100점)
    private Integer overallScore;           // 전체 평가 점수 (0-100점)
    private String overallReason;           // 종합 평가 이유

    public ImageAnalysisResponseDTO() {}

    // 전체 생성자
    public ImageAnalysisResponseDTO(boolean success, String message, String guess, 
                                  Integer confidence, List<String> otherPossibilities, 
                                  String reason, Integer drawingQualityScore, 
                                  Integer creativityScore, Integer overallScore,
                                  String overallReason) {
        this.success = success;
        this.message = message;
        this.guess = guess;
        this.confidence = confidence;
        this.otherPossibilities = otherPossibilities;
        this.reason = reason;
        this.drawingQualityScore = drawingQualityScore;
        this.creativityScore = creativityScore;
        this.overallScore = overallScore;
        this.overallReason = overallReason;
    }

    // 성공 응답용 생성자 (게임 결과 포함)
    public static ImageAnalysisResponseDTO success(String guess, Integer confidence, 
                                                 List<String> otherPossibilities, 
                                                 String reason, Integer drawingQualityScore,
                                                 Integer creativityScore, Integer overallScore,
                                                 String overallReason) {
        return new ImageAnalysisResponseDTO(true, "스케치 분석이 완료되었습니다!", 
                                          guess, confidence, otherPossibilities, reason,
                                          drawingQualityScore, creativityScore, overallScore, overallReason);
    }

    // 실패 응답용 생성자
    public static ImageAnalysisResponseDTO failure(String message) {
        return new ImageAnalysisResponseDTO(false, message, null, null, null, null, null, null, null, null);
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }

    public Integer getConfidence() {
        return confidence;
    }

    public void setConfidence(Integer confidence) {
        this.confidence = confidence;
    }

    public List<String> getOtherPossibilities() {
        return otherPossibilities;
    }

    public void setOtherPossibilities(List<String> otherPossibilities) {
        this.otherPossibilities = otherPossibilities;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getDrawingQualityScore() {
        return drawingQualityScore;
    }

    public void setDrawingQualityScore(Integer drawingQualityScore) {
        this.drawingQualityScore = drawingQualityScore;
    }

    public Integer getCreativityScore() {
        return creativityScore;
    }

    public void setCreativityScore(Integer creativityScore) {
        this.creativityScore = creativityScore;
    }

    public Integer getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Integer overallScore) {
        this.overallScore = overallScore;
    }

    public String getOverallReason() {
        return overallReason;
    }

    public void setOverallReason(String overallReason) {
        this.overallReason = overallReason;
    }
} 