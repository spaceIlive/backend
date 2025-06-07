package com.backend.application;

import com.backend.domain.Drawing;
import com.backend.domain.Folder;
import com.backend.dto.DrawingResponseDTO;
import com.backend.infrastructure.DrawingRepository;
import com.backend.infrastructure.FolderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DrawingService {

    private final DrawingRepository drawingRepository;
    private final FolderRepository folderRepository;
    
    //생성자 주입
    public DrawingService(DrawingRepository drawingRepository, FolderRepository folderRepository) {
        this.drawingRepository = drawingRepository;
        this.folderRepository = folderRepository;
    }

    // DTO 변환 메서드 추가
    private DrawingResponseDTO convertToDTO(Drawing drawing) {
        return DrawingResponseDTO.builder()
                .drawing_id(drawing.getDrawing_id())
                .title(drawing.getTitle())
                .content(drawing.getContent())
                .folder_id(drawing.getFolder().getFolder_id())
                .createdAt(drawing.getCreatedAt())
                .updatedAt(drawing.getUpdatedAt())
                .build();
    }

    // 스케치 생성
    public DrawingResponseDTO createDrawing(String title, String content, Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("폴더를 찾을 수 없습니다."));

        Drawing drawing = Drawing.builder()
                .title(title)
                .content(content)
                .folder(folder)
                .build();

        return convertToDTO(drawingRepository.save(drawing));
    }

    // ID로 스케치 조회
    private Drawing getDrawingById(Long drawing_id) {
        return drawingRepository.findById(drawing_id)
                .orElseThrow(() -> new IllegalArgumentException("스케치를 찾을 수 없습니다."));
    }

    // 폴더 내 모든 스케치 조회
    public List<DrawingResponseDTO> getDrawingsByFolder(Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("폴더를 찾을 수 없습니다."));
        return drawingRepository.findByFolder(folder).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 스케치 수정
    public DrawingResponseDTO updateDrawing(Long drawing_id, String title, String content) {
        Drawing drawing = getDrawingById(drawing_id);
        drawing.setTitle(title);
        drawing.setContent(content);
        return convertToDTO(drawingRepository.save(drawing));
    }

    // 스케치 삭제
    public void deleteDrawing(Long drawing_id) {
        drawingRepository.deleteById(drawing_id);
    }
}
