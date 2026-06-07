package com.example.babysense_server.controller;

import com.example.babysense_server.dto.NoiseRequestDto;
import com.example.babysense_server.entity.SleepNoiseLog;
import com.example.babysense_server.repository.SleepNoiseLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sleep-records")
public class NoiseController {

    private final SleepNoiseLogRepository noiseLogRepository;

    public NoiseController(SleepNoiseLogRepository noiseLogRepository) {
        this.noiseLogRepository = noiseLogRepository;
    }

    /**
     * Flutter 앱으로부터 배치 소음 데이터를 수신하여 벌크 저장합니다.
     */
    @PostMapping("/{recordId}/noise")
    @Transactional // 대량의 데이터 저장을 하나의 트랜잭션으로 안전하게 처리
    public ResponseEntity<String> receiveNoiseLogs(
            @PathVariable Long recordId,
            @RequestBody NoiseRequestDto requestDto) {

        if (requestDto.getNoise_logs() == null || requestDto.getNoise_logs().isEmpty()) {
            return ResponseEntity.badRequest().body("전송된 소음 데이터가 없습니다.");
        }

        // DTO 리스트를 Entity 리스트로 변환
        List<SleepNoiseLog> entities = requestDto.getNoise_logs().stream()
                .map(dto -> new SleepNoiseLog(recordId, dto.getMeasured_at(), dto.getDecibel()))
                .collect(Collectors.toList());

        // 벌크 인서트 수행
        noiseLogRepository.saveAll(entities);

        return ResponseEntity.ok("소음 데이터 " + entities.size() + "건이 성공적으로 저장되었습니다.");
    }

    /**
     * 특정 수면 기록의 소음 분석 리포트(가이드 문구)를 반환합니다.
     * (현재는 프론트엔드 UI 연동 테스트를 위해 고정된 임시 문자열을 반환합니다.)
     */
    @GetMapping("/{recordId}/analysis")
    public ResponseEntity<String> getNoiseAnalysis(@PathVariable Long recordId) {
        // 추후 SleepNoiseLogRepository를 이용해 DB 데이터를 조회하고 분석하는 로직으로 대체해야 합니다.
        String dummyReport = "분석 결과입니다.\n"
                + "밤새 평균 소음은 45dB로 조용한 편이었습니다.\n"
                + "아기가 깨지 않도록 현재 환경을 잘 유지해 주세요!";

        return ResponseEntity.ok(dummyReport);
    }
}