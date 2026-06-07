package com.example.babysense_server.controller;

import com.example.babysense_server.entity.User; // 경로 명 수정완료
import com.example.babysense_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody User user) {
        System.out.println("가입 요청 이메일: " + user.getEmail());

        // 1. DB에 이미 똑같은 이메일이 있는지 검색 (핵심 중복 검사)
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()) {
            // 2. 이미 가입된 이메일이라면 저장을 막고 상태코드 409(Conflict) 반환
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 이메일입니다.");
        }

        // 3. 중복이 없다면 정상적으로 가입(저장) 처리 후 상태코드 200(OK) 반환
        userRepository.save(user);
        return ResponseEntity.ok("회원가입 성공!");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User loginUser) {
        System.out.println("로그인 시도 이메일: " + loginUser.getEmail());

        // 1. DB에서 이메일로 사용자 검색
        Optional<User> userOptional = userRepository.findByEmail(loginUser.getEmail());

        // 2. 사용자가 존재하면 비밀번호 비교
        if (userOptional.isPresent()) {
            User dbUser = userOptional.get();

            if (dbUser.getPassword().equals(loginUser.getPassword())) {
                // 이메일과 비밀번호가 모두 일치할 때 (상태코드 200 반환)
                return ResponseEntity.ok("로그인 성공!");
            } else {
                // 비밀번호가 틀렸을 때 (상태코드 401 반환)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 틀렸습니다.");
            }
        } else {
            // 이메일 자체가 없을 때 (상태코드 404 반환)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("가입되지 않은 이메일입니다.");
        }
    }
}