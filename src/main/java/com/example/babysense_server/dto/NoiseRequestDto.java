package com.example.babysense_server.dto;

import java.time.LocalDateTime;
import java.util.List;

public class NoiseRequestDto {
    private List<NoiseLogDto> noise_logs;

    public List<NoiseLogDto> getNoise_logs() {
        return noise_logs;
    }

    public void setNoise_logs(List<NoiseLogDto> noise_logs) {
        this.noise_logs = noise_logs;
    }

    public static class NoiseLogDto {
        private LocalDateTime measured_at;
        private double decibel;

        public LocalDateTime getMeasured_at() {
            return measured_at;
        }

        public void setMeasured_at(LocalDateTime measured_at) {
            this.measured_at = measured_at;
        }

        public double getDecibel() {
            return decibel;
        }

        public void setDecibel(double decibel) {
            this.decibel = decibel;
        }
    }
}