package com.example.babysense_server.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users") // MySQL의 users 테이블과 연결
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(name = "password_hash")
    private String password;
    private String name;

    // 빨간 줄 해결: 직접 추가하는 Getter/Setter
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}