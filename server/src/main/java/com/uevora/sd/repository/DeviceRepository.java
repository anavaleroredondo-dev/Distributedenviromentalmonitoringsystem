package com.uevora.sd.repository;

import com.uevora.sd.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    // Spring Boot implementará automáticamente métodos como save(), findById(), findAll(), etc.
}