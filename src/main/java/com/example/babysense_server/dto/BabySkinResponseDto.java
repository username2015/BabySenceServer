package com.example.babysense_server.dto;

public class BabySkinResponseDto {
    private String status;
    private String disease;
    private double probability;

    // 기본 생성자, Getter, Setter
    public BabySkinResponseDto() {}

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDisease() { return disease; }
    public void setDisease(String disease) { this.disease = disease; }

    public double getProbability() { return probability; }
    public void setProbability(double probability) { this.probability = probability; }
}