package com.backend.presentation;

import com.backend.application.FolderService;
import com.backend.dto.FolderResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    private final FolderService folderService;

    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    // 폴더 생성
    @PostMapping
    public ResponseEntity<FolderResponseDTO> createFolder(
            @RequestParam Long userId,
            @RequestParam String name,
            @RequestParam(required = false) Long parentId
    ) {
        try {
            FolderResponseDTO folder = folderService.createFolder(userId, name, parentId);
            return ResponseEntity.ok(folder);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 전체 폴더 구조와 스케치 파일 조회
    @GetMapping("/structure/{userId}")
    public ResponseEntity<FolderResponseDTO> getFolderStructure(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(folderService.getFolderStructure(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 폴더 삭제
    @DeleteMapping("/{folderId}")
    public ResponseEntity<Void> deleteFolder(@PathVariable Long folderId) {
        try {
            folderService.deleteFolder(folderId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
