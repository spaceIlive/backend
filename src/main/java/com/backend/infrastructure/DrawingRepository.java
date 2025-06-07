package com.backend.infrastructure;

import com.backend.domain.Drawing;
import com.backend.domain.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DrawingRepository extends JpaRepository<Drawing, Long> {
    
    // 특정 폴더의 모든 그림 조회
    List<Drawing> findByFolder(Folder folder);
    
    // 특정 폴더의 모든 그림 삭제
    void deleteByFolder(Folder folder);
}
