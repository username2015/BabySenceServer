package com.example.babysense_server.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class BabyCryService {

    private final String AI_SERVER_URL = "http://localhost:5001/predict";
    private final RestTemplate restTemplate = new RestTemplate();

    public String analyzeBabyCry(MultipartFile multipartFile) throws IOException {
        // 1. 프로젝트 내부 안전한 경로에 temp 폴더 자동 생성
        String uploadDir = System.getProperty("user.dir") + File.separator + "temp";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 2. 고유한 파일명으로 진짜 파일 생성
        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null) originalFilename = "baby_cry.wav";

        Path targetPath = Paths.get(uploadDir, System.currentTimeMillis() + "_" + originalFilename);
        File targetFile = targetPath.toFile();

        // 3. 받은 바이트 데이터를 파일로 이식
        multipartFile.transferTo(targetFile);

        // 4. 헤더 및 멀티파트 바디 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(targetFile));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            // 5. 파이썬 서버로 파일 토스
            Map<String, Object> response = restTemplate.postForObject(AI_SERVER_URL, requestEntity, Map.class);

            if (response != null && response.containsKey("result")) {
                return (String) response.get("result");
            }
            return "unknown";

        } finally {
            // 6. 분석 완료 후 생성했던 로컬 파일은 깔끔하게 즉시 삭제
            if (targetFile.exists()) {
                targetFile.delete();
            }
        }
    }
}