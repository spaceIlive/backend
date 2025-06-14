package com.backend.dto;

public class ImageAnalysisResponseDTO {
    private String result;
    private boolean success;
    private String message;

    public ImageAnalysisResponseDTO() {}

    public ImageAnalysisResponseDTO(String result, boolean success, String message) {
        this.result = result;
        this.success = success;
        this.message = message;
    }

    // 성공 응답용 생성자
    public static ImageAnalysisResponseDTO success(String result) {
        return new ImageAnalysisResponseDTO(result, true, "분석이 완료되었습니다.");
    }

    // 실패 응답용 생성자
    public static ImageAnalysisResponseDTO failure(String message) {
        return new ImageAnalysisResponseDTO(null, false, message);
    }

    // Getters and Setters
    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

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
} 