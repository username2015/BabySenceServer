package com.example.babysense_server.repository;

import com.example.babysense_server.entity.BabySkinLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BabySkinLogRepository extends JpaRepository<BabySkinLog, Long> {
}