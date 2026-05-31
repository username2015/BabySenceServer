package com.example.babysense_server.repository;

import com.example.babysense_server.entity.SleepNoiseLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SleepNoiseLogRepository extends JpaRepository<SleepNoiseLog, Long> {
    // 추후 특정 수면 기록의 모든 소음 데이터를 조회할 때 사용 가능
    // List<SleepNoiseLog> findBySleepRecordId(Long sleepRecordId);
}