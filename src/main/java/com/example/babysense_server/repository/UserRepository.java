package com.example.babysense_server.repository;

import com.example.babysense_server.entity.User; // 본인의 User 엔티티 경로
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    // 이것만 적어두면 JPA가 알아서 저장(save), 조회(find) 기능을 다 해줌
}