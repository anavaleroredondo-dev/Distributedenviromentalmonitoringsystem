package com.uevora.sd.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "metrics")
public class Metric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID interno de la base de datos

    private String deviceId; // ID del dispositivo que envió el dato
    private double temperature;
    private double humidity;
    private LocalDateTime timestamp;

    public Metric() {}

    public Metric(String deviceId, double temperature, double humidity, LocalDateTime timestamp) {
        this.deviceId = deviceId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.timestamp = timestamp;
    }

    // Getters
    public Long getId() { return id; }
    public String getDeviceId() { return deviceId; }
    public double getTemperature() { return temperature; }
    public double getHumidity() { return humidity; }
    public LocalDateTime getTimestamp() { return timestamp; }
}