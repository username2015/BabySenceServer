package com.example.babysense_server.controller;

import com.example.babysense_server.service.BabyCryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cry")
public class BabyCryController {

    private final BabyCryService babyCryService;

    public BabyCryController(BabyCryService babyCryService) {
        this.babyCryService = babyCryService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, String>> analyzePost(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            String BOUNDARY = "===JavaMultipartBoundary===";
            URL url = new URL("http://localhost:5001/api/cry/analyze");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            try (DataOutputStream requestStream = new DataOutputStream(conn.getOutputStream())) {
                requestStream.writeBytes("--" + BOUNDARY + "\r\n");
                requestStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getOriginalFilename() + "\"\r\n");
                requestStream.writeBytes("Content-Type: " + file.getContentType() + "\r\n\r\n");

                requestStream.write(file.getBytes());
                requestStream.writeBytes("\r\n");
                requestStream.writeBytes("--" + BOUNDARY + "--\r\n");
                requestStream.flush();
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String inputLine;
                    StringBuilder responseBuffer = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        responseBuffer.append(inputLine);
                    }

                    String pythonResponse = responseBuffer.toString().toLowerCase(); // 소문자 변환으로 안정성 극대화
                    System.out.println("[AI 실시간 수신 로그] 파이썬 서버 응답: " + pythonResponse);

                    String rawResult = "hungry"; // 기본값
                    if (pythonResponse.contains("belly_pain")) rawResult = "belly_pain";
                    else if (pythonResponse.contains("discomfort")) rawResult = "discomfort";
                    else if (pythonResponse.contains("burping")) rawResult = "burping";
                    else if (pythonResponse.contains("hungry")) rawResult = "hungry";
                    else if (pythonResponse.contains("tired")) rawResult = "tired";

                    // 2. 영어 결과를 한국어로 최종 매핑
                    String koreanResult;
                    switch (rawResult) {
                        case "hungry":
                            koreanResult = "배고픔";
                            break;
                        case "tired":
                            koreanResult = "졸림";
                            break;
                        case "discomfort":
                            koreanResult = "불편함 (기저귀/온도 등)";
                            break;
                        case "belly_pain":
                            koreanResult = "복통";
                            break;
                        case "burping":
                            koreanResult = "트림 필요";
                            break;
                        default:
                            koreanResult = "배고픔";
                            break;
                    }

                    response.put("status", "success");
                    response.put("analysis", koreanResult);
                    return ResponseEntity.ok(response);
                }
            } else {
                response.put("status", "error");
                response.put("message", "파이썬 서버 응답 실패 코드: " + responseCode);
                return ResponseEntity.internalServerError().body(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}