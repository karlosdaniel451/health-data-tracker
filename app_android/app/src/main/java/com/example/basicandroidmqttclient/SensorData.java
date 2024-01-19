package com.example.basicandroidmqttclient;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SensorData {
    private String timestamp;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal noiseLevel;
    private Integer heartFrequency;

    public SensorData(Date timestamp, BigDecimal temperature, BigDecimal humidity, BigDecimal noiseLevel, Integer heartFrequency) {
        this.timestamp = SensorData.dateToTimestampString(timestamp);
        this.temperature = temperature;
        this.humidity = humidity;
        this.noiseLevel = noiseLevel;
        this.heartFrequency = heartFrequency;
    }

    public SensorData(Date timestamp, BigDecimal temperature, BigDecimal humidity, BigDecimal noiseLevel) {
        this.timestamp = SensorData.dateToTimestampString(timestamp);
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

    public static Date timestampStringToDate(String timestampString) {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date timestamp = null;
        try {
            timestamp = isoFormat.parse(timestampString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }

    public static String dateToTimestampString(Date timestamp) {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        isoFormat.setTimeZone(TimeZone.getDefault());
        return isoFormat.format(timestamp);
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
}
