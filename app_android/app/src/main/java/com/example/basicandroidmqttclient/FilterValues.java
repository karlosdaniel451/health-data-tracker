package com.example.basicandroidmqttclient;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FilterValues {
    private String startDateTime;
    private String endDateTime;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal noise;
    private Integer heartFrequency;
    private String temperatureCondition;
    private String humidityCondition;
    private String noiseCondition;
    private String heartFrequencyCondition;

    public FilterValues() {
    }

    public FilterValues(String startDateTime, String endDateTime, BigDecimal temperature,
                        BigDecimal humidity, BigDecimal noise, Integer heartFrequency,
                        String temperatureCondition, String humidityCondition, String noiseCondition,
                        String heartFrequencyCondition) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.temperature = temperature;
        this.humidity = humidity;
        this.noise = noise;
        this.heartFrequency = heartFrequency;
        this.temperatureCondition = temperatureCondition;
        this.humidityCondition = humidityCondition;
        this.noiseCondition = noiseCondition;
        this.heartFrequencyCondition = heartFrequencyCondition;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public BigDecimal getHumidity() {
        return humidity;
    }

    public void setHumidity(BigDecimal humidity) {
        this.humidity = humidity;
    }

    public BigDecimal getNoise() {
        return noise;
    }

    public void setNoise(BigDecimal noise) {
        this.noise = noise;
    }

    public Integer getHeartFrequency() {
        return heartFrequency;
    }

    public void setHeartFrequency(Integer heartFrequency) {
        this.heartFrequency = heartFrequency;
    }

    public String getTemperatureCondition() {
        return temperatureCondition;
    }

    public void setTemperatureCondition(String temperatureCondition) {
        this.temperatureCondition = temperatureCondition;
    }

    public String getHumidityCondition() {
        return humidityCondition;
    }

    public void setHumidityCondition(String humidityCondition) {
        this.humidityCondition = humidityCondition;
    }

    public String getNoiseCondition() {
        return noiseCondition;
    }

    public void setNoiseCondition(String noiseCondition) {
        this.noiseCondition = noiseCondition;
    }

    public String getHeartFrequencyCondition() {
        return heartFrequencyCondition;
    }

    public void setHeartFrequencyCondition(String heartFrequencyCondition) {
        this.heartFrequencyCondition = heartFrequencyCondition;
    }
}
