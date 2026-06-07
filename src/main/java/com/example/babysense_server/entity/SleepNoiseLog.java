package com.example.babysense_server.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sleep_noise_logs")
public class SleepNoiseLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;

    // 수면 기록 ID (기존 sleep_records 테이블의 PK가 될 값, 여기서는 단순 외래키 ID 매핑으로 처리)
    @Column(name = "record_id", nullable = false)
    private Long sleepRecordId;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @Column(nullable = false)
    private double decibel;

    // 기본 생성자
    public SleepNoiseLog() {}

    public SleepNoiseLog(Long sleepRecordId, LocalDateTime measuredAt, double decibel) {
        this.sleepRecordId = sleepRecordId;
        this.measuredAt = measuredAt;
        this.decibel = decibel;
    }

    // Getters and Setters
    public Long getLogId() { return logId; }
    public Long getSleepRecordId() { return sleepRecordId; }
    public LocalDateTime getMeasuredAt() { return measuredAt; }
    public double getDecibel() { return decibel; }
}