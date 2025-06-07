package com.backend.application;

import com.backend.domain.Folder;
import com.backend.domain.User;
import com.backend.dto.DrawingResponseDTO;
import com.backend.dto.FolderResponseDTO;
import com.backend.infrastructure.FolderRepository;
import com.backend.infrastructure.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

    public FolderService(FolderRepository folderRepository, UserRepository userRepository) {
        this.folderRepository = folderRepository;
        this.userRepository = userRepository;
    }

    // 사용자의 루트 폴더 조회
    private Folder getRootFolder(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return folderRepository.findByUserAndParentIsNull(user)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("루트 폴더를 찾을 수 없습니다."));
    }

    // 재귀적으로 모든 하위 폴더와 스케치들을 초기화
    private void initializeCollections(Folder folder) {
        if (folder.getChildren() != null) {
            folder.getChildren().forEach(child -> {
                if (child.getDrawings() != null) {
                    child.getDrawings().size();
                }
                initializeCollections(child);
            });
        }
    }

    // DTO 변환 메서드
    private FolderResponseDTO convertToDTO(Folder folder) {
        return FolderResponseDTO.builder()
                .folder_id(folder.getFolder_id())
                .name(folder.getName())
                .parent_id(folder.getParent() != null ? folder.getParent().getFolder_id() : null)
                .user_id(folder.getUser().getId())
                .children(folder.getChildren() != null ? 
                    folder.getChildren().stream()
                            .map(this::convertToDTO)
                            .collect(Collectors.toList()) : 
                    java.util.Collections.emptyList())
                .drawings(folder.getDrawings() != null ? 
                    folder.getDrawings().stream()
                            .map(drawing -> DrawingResponseDTO.builder()
                                    .drawing_id(drawing.getDrawing_id())
                                    .title(drawing.getTitle())
                                    .content(drawing.getContent())
                                    .folder_id(drawing.getFolder().getFolder_id())
                                    .createdAt(drawing.getCreatedAt())
                                    .updatedAt(drawing.getUpdatedAt())
                                    .build())
                            .collect(Collectors.toList()) : 
                    java.util.Collections.emptyList())
                .createdAt(folder.getCreatedAt())
                .updatedAt(folder.getUpdatedAt())
                .build();
    }

    // getFolderStructure 메서드 수정
    public FolderResponseDTO getFolderStructure(Long userId) {
        Folder rootFolder = getRootFolder(userId);
        
        // 하위 폴더들을 강제 초기화
        if (rootFolder.getChildren() != null) {
            rootFolder.getChildren().size();
        }
        // 스케치들을 강제 초기화
        if (rootFolder.getDrawings() != null) {
            rootFolder.getDrawings().size();
        }
        // 재귀적으로 모든 하위 폴더의 컬렉션들을 초기화
        initializeCollections(rootFolder);
        
        return convertToDTO(rootFolder);
    }

    // 폴더 생성 (이제 모든 폴더는 루트 폴더의 하위 폴더로 생성됨)
    public FolderResponseDTO createFolder(Long userId, String name, Long parent_id) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // parent_id가 null이면 루트 폴더 아래에 생성
        Folder parent_folder;
        if (parent_id == null) {
            parent_folder = getRootFolder(userId);
        } else {
            parent_folder = folderRepository.findById(parent_id)
                    .orElseThrow(() -> new IllegalArgumentException("부모 폴더를 찾을 수 없습니다."));
            
            // 부모 폴더의 소유자 체크
            if (!parent_folder.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("잘못된 부모 폴더입니다.");
            }
        }

        // 동일 계층에서 같은 이름을 가진 폴더가 있는지 확인하고 처리
        String finalFolderName = name;
        int counter = 1;
        while (true) {
            // 같은 부모를 가진 폴더 중에서 같은 이름이 있는지 확인
            boolean exists = folderRepository.findByNameAndUserAndParent(finalFolderName, user, parent_folder).isPresent();
            if (!exists) {
                break;
            }
            finalFolderName = name + "(" + counter + ")";
            counter++;
        }

        Folder folder = Folder.builder()
                .name(finalFolderName)
                .user(user)
                .parent(parent_folder)
                .build();

        return convertToDTO(folderRepository.save(folder));
    }

    // 폴더 이름 수정
    public FolderResponseDTO updateFolderName(Long folderId, String newName) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("폴더를 찾을 수 없습니다."));
        
        // 루트 폴더는 이름 변경 불가
        if (folder.getParent() == null) {
            throw new IllegalArgumentException("루트 폴더의 이름은 변경할 수 없습니다.");
        }
        
        // 같은 부모를 가진 폴더들 중에서만 이름 중복 체크
        folderRepository.findByNameAndUserAndParent(newName, folder.getUser(), folder.getParent())
                .ifPresent(f -> {
                    if (!f.getFolder_id().equals(folderId)) {
                        throw new IllegalArgumentException("같은 위치에 이미 동일한 이름의 폴더가 존재합니다.");
                    }
                });

        folder.setName(newName);
        return convertToDTO(folderRepository.save(folder));
    }

    // 폴더 삭제
    public void deleteFolder(Long folderId) {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(() -> new IllegalArgumentException("폴더를 찾을 수 없습니다."));
        
        // 루트 폴더는 삭제 불가
        if (folder.getParent() == null) {
            throw new IllegalArgumentException("루트 폴더는 삭제할 수 없습니다.");
        }
        
        folderRepository.delete(folder);
    }
}
