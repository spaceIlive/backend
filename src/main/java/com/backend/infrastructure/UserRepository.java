package com.backend.infrastructure;

import com.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email); // JPA가 자동으로 쿼리 만들어줌 email기준으로
}

//  JPA 인터페이스 기본적으로 User 엔티티에 대한 기본적인 CRUD 기능을 제공