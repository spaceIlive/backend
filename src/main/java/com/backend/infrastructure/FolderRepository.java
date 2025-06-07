package com.backend.infrastructure;

import com.backend.domain.Folder;
import com.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    
    // 특정 사용자의 폴더 조회
    List<Folder> findByUser(User user);

    // 중복방지위해 추가
    Optional<Folder> findByNameAndUser(String name, User user);
    
    // 동일 계층 내 폴더 이름 중복 확인을 위한 메서드
    Optional<Folder> findByNameAndUserAndParent(String name, User user, Folder parent);
    
    // 루트 폴더 조회
    List<Folder> findByUserAndParentIsNull(User user);
    
    // 특정 폴더의 직계 하위 폴더 조회
    List<Folder> findByUserAndParent(User user, Folder parent);
}
