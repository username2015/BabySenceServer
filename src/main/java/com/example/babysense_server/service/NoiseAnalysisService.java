package com.example.babysense_server.service;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NoiseAnalysisService {

    // 소음 임계값 설정 (예: 55dB)
    private static final double NOISE_THRESHOLD = 55.0;

    /**
     * 특정 수면 기록의 소음 데이터를 분석하여 깨어날 확률을 계산하고 가이드 문구를 반환합니다.
     * (참고: DB 조회 로직은 Repository가 구현되어 있다는 추측 하에 작성된 예시입니다.)
     */
    public String generateNoiseAnalysisReport(Long sleepRecordId) {
        // 1. DB에서 해당 수면 기록의 소음 로그와 깬 시점 데이터를 가져옵니다.
        // List<SleepNoiseLog> noiseLogs = noiseRepository.findBySleepRecordId(sleepRecordId);
        // List<WakeUpLog> wakeUpLogs = wakeUpRepository.findBySleepRecordId(sleepRecordId);

        // --- 더미 데이터 연산 (실제로는 위 DB 데이터를 활용) ---
        int totalHighNoiseEvents = 15; // 55dB을 넘은 총 횟수
        int wakeUpsTriggeredByNoise = 12; // 55dB 돌파 직후(예: 5분 이내) 아기가 깬 횟수

        // 2. 55dB 이상 이벤트가 충분하지 않다면 분석 보류
        if (totalHighNoiseEvents < 5) {
            return "아직 소음 데이터를 분석할 만큼 충분한 기록이 모이지 않았어요. 수면 모드를 더 활용해 주세요!";
        }

        // 3. 확률 계산 (통계 연산)
        double probability = ((double) wakeUpsTriggeredByNoise / totalHighNoiseEvents) * 100;
        int probPercentage = (int) Math.round(probability);

        // 4. 확률에 따른 100% 규칙 기반(Rule-based) 문구 템플릿 반환 (AI 토큰 0 소모)
        if (probPercentage >= 70) {
            return String.format(
                    "분석 결과, 주변 소음이 %.0fdB 이상으로 올라갈 때 스치가 잠에서 깰 확률이 %d%%로 매우 높습니다. 백색소음기를 틀어 일정한 소음 환경을 만들어주세요.",
                    NOISE_THRESHOLD, probPercentage
            );
        } else if (probPercentage >= 40) {
            return String.format(
                    "주변 소음이 %.0fdB 이상일 때 깰 확률이 %d%%입니다. 갑작스러운 생활 소음에 주의해 주세요.",
                    NOISE_THRESHOLD, probPercentage
            );
        } else {
            return "현재 수면 환경의 소음은 스치의 수면에 큰 영향을 주지 않는 것으로 분석됩니다. 잘하고 계십니다!";
        }
    }
}