package com.example.babysense_server.controller;

import com.example.babysense_server.entity.User; // 경로 명 수정완료
import com.example.babysense_server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/join")
    public String join(@RequestBody User user) {
        System.out.println("가입 요청 이메일: " + user.getEmail());

        userRepository.save(user); // 실제 DB 저장 실행
        return "회원가입 성공!";
    }
}