package com.uevora.sd.model;

import jakarta.persistence.*;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    private String id; // El ID lo define el usuario (ej: "sensor-sala-1")

    @Enumerated(EnumType.STRING)
    private DeviceProtocol protocol; // MQTT, GRPC, REST

    // Localización
    private String room;
    private String department;
    private String floor;
    private String building;

    private boolean active = true; //Esto asegura que al crearse sea 'activo' por defecto

    public Device() {}

    public Device(String id, DeviceProtocol protocol, String room, String department, String floor, String building) {
        this.id = id;
        this.protocol = protocol;
        this.room = room;
        this.department = department;
        this.floor = floor;
        this.building = building;
        this.active = true;
    }

    // Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public DeviceProtocol getProtocol() { return protocol; }
    public void setProtocol(DeviceProtocol protocol) { this.protocol = protocol; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }
    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}