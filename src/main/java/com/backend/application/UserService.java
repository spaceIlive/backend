package com.backend.application;

import com.backend.domain.Folder;
import com.backend.domain.User;
import com.backend.dto.UserResponseDTO;
import com.backend.infrastructure.FolderRepository;
import com.backend.infrastructure.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, FolderRepository folderRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.folderRepository = folderRepository;
        this.passwordEncoder = passwordEncoder;
    }
    // User 엔티티를 DTO로 변환
    private UserResponseDTO convertToDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );
    }
    // 사용자 생성
    public UserResponseDTO createUser(User user) {
        // 중복 이메일 검사
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }

        // 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 사용자 저장
        User savedUser = userRepository.save(user);

        // "문서" 루트 폴더 생성
        Folder rootFolder = Folder.builder()
                .name("문서")
                .user(savedUser)
                .parent(null)
                .build();
        folderRepository.save(rootFolder);

        return convertToDTO(savedUser);
    }

    // 로그인
    public Optional<UserResponseDTO> login(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPassword()))
                .map(this::convertToDTO);
    }

    // 사용자 삭제 (연관된 모든 데이터가 자동으로 삭제됨)
    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            // User 엔티티의 @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)로 인해
            // 연관된 폴더와 스케치가 자동으로 삭제됨
            userRepository.delete(user);
        });
    }

    // 비밀번호 변경
    public void updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 암호화 및 저장
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
