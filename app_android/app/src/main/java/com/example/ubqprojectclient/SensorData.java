package com.example.ubqprojectclient;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class SensorData {
    private String timestamp;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal noiseLevel;
    private Integer heartFrequency;

    public SensorData(Date timestamp, BigDecimal temperature, BigDecimal humidity, BigDecimal noiseLevel, Integer heartFrequency) {
        this.timestamp = DateUtils.dateToString(timestamp);
        this.temperature = temperature;
        this.humidity = humidity;
        this.noiseLevel = noiseLevel;
        this.heartFrequency = heartFrequency;
    }

    public SensorData(Date timestamp, BigDecimal temperature, BigDecimal humidity, BigDecimal noiseLevel) {
        this.timestamp = DateUtils.dateToString(timestamp);
        this.temperature = temperature;
        this.humidity = humidity;
        this.noiseLevel = noiseLevel;
    }

    public SensorData(String timestamp, BigDecimal temperature, BigDecimal humidity, BigDecimal noiseLevel) {
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.noiseLevel = noiseLevel;
    }

    // Getters
    public String getTimestamp() {
        return timestamp;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public BigDecimal getHumidity() {
        return humidity;
    }

    public BigDecimal getNoiseLevel() {
        return noiseLevel;
    }

    public Integer getHeartFrequency() {
        return heartFrequency;
    }

    public BigDecimal getSensorValue(String dashboardTitle) {
        switch (dashboardTitle) {
            case "Temperature":
                return this.getTemperature();
            case "Humidity":
                return this.getHumidity();
            case "Noise Level":
                return this.getNoiseLevel();
            case "Heart Frequency":
                if (this.getHeartFrequency() != null) {
                    return BigDecimal.valueOf(this.getHeartFrequency());
                }
        }
        return null;
    }
}
