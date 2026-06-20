package com.example.babysense_server.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "baby_skin_log")
public class BabySkinLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String diseaseResult;
    private double probability;
    private LocalDateTime createdAt;

    public BabySkinLog() {}

    public BabySkinLog(String diseaseResult, double probability) {
        this.diseaseResult = diseaseResult;
        this.probability = probability;
        this.createdAt = LocalDateTime.now();
    }

    // Getter
    public Long getId() { return id; }
    public String getDiseaseResult() { return diseaseResult; }
    public double getProbability() { return probability; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}