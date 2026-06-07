package com.example.babysense_server.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id") // ⭐️ 이 줄을 추가해서 SQL의 user_id와 연결해줍니다!
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(name = "password_hash")
    private String password;

    private String name;

    // Getter/Setter는 그대로 두셔도 됩니다.
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}