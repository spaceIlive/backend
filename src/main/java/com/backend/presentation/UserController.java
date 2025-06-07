package com.backend.presentation;

import com.backend.application.UserService;
import com.backend.domain.User;
import com.backend.dto.LoginRequestDTO;
import com.backend.dto.PasswordUpdateDTO;
import com.backend.dto.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")  // 모든 요청은 /api/users 하위에서 처리됨
public class UserController {

    private final UserService userService;

    // 생성자 주입
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //로그인 로직 (POST /api/users/login) 보안을 위해서 post로 body에 넣어서 보냄
    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@RequestBody LoginRequestDTO loginDto) {
        return userService.login(loginDto.getEmail(), loginDto.getPassword())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    // 사용자 생성 (POST /api/users)
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody User user) {
        UserResponseDTO saved = userService.createUser(user);
        return ResponseEntity.ok(saved);
    }

    // 비밀번호 변경 (PATCH /api/users/{id}/password)
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordUpdateDTO passwordDto
    ) {
        userService.updatePassword(id, passwordDto.getCurrentPassword(), passwordDto.getNewPassword());
        return ResponseEntity.ok().build();
    }

    // 사용자 삭제 (DELETE /api/users/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
