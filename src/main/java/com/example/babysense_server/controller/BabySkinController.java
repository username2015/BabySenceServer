package com.example.babysense_server.controller;

import com.example.babysense_server.dto.BabySkinResponseDto;
import com.example.babysense_server.entity.BabySkinLog;
import com.example.babysense_server.repository.BabySkinLogRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/skin")
public class BabySkinController {

    private final BabySkinLogRepository babySkinLogRepository;
    private final WebClient webClient;

    // 생성자 주입
    public BabySkinController(BabySkinLogRepository babySkinLogRepository) {
        this.babySkinLogRepository = babySkinLogRepository;

        // 💡 [버그 방지 추가] 파이썬 서버로 보낼 때 WebClient 자체 버퍼 용량도 100MB로 늘려줍니다.
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(100 * 1024 * 1024))
                .build();

        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8000") // 파이썬 FastAPI 주소
                .exchangeStrategies(strategies)
                .build();
    }

    // 💡 [버그 방지 추가] consumes 속성을 꽂아서 대용량 멀티파트 데이터를 확실하게 명시합니다.
    @PostMapping(value = "/diagnose", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> diagnoseSkin(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 비어있습니다.");
        }

        try {
            // 1. 플러터가 보낸 파일을 파이썬 전송용 Multipart 양식으로 변환
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename(); // 원본 파일명 유지
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            // 2. WebClient를 사용해 파이썬 FastAPI 서버의 /predict 엔진 호출
            BabySkinResponseDto aiResult = webClient.post()
                    .uri("/predict")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(BabySkinResponseDto.class)
                    .block(); // AI 결과를 받아올 때까지 대기

            if (aiResult == null || !"success".equals(aiResult.getStatus())) {
                return ResponseEntity.status(500).body("AI 서버 연동 실패");
            }

            // 3. 우리가 설계한 예외 처리 임계값(Cut-off) 로직 연동
            if (aiResult.getProbability() < 50.0) {
                return ResponseEntity.ok().body("{\"message\": \"정확한 판독이 어렵습니다. 깨끗한 조명에서 환부를 다시 촬영해 주세요.\"}");
            }

            BabySkinLog log = new BabySkinLog(aiResult.getDisease(), aiResult.getProbability());
            babySkinLogRepository.save(log);

            // 5. 최종 성공 결과를 플러터로 리턴
            return ResponseEntity.ok(aiResult);

        } catch (IOException e) {
            return ResponseEntity.status(500).body("파일 처리 중 에러 발생: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("AI 서버 통신 중 에러 발생: " + e.getMessage());
        }
    }
}