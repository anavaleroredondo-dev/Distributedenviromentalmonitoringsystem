package com.uevora.sd.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import java.util.Random;

@SpringBootApplication
public class RestSimulatorApplication implements CommandLineRunner {

    private static final String SERVER_URL = "http://localhost:8081/api";
    private static final String DEVICE_ID = "sensor-rest-01"; // ID único del dispositivo [cite: 60]

    public static void main(String[] args) {
        SpringApplication.run(RestSimulatorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        Random random = new Random();

        // 1. Registrar el dispositivo en el servidor [cite: 102]
        DeviceDTO newDevice = new DeviceDTO(DEVICE_ID, "REST", "Sala 101", "Informatica", "Piso 1", "Edificio B");
        try {
            restTemplate.postForObject(SERVER_URL + "/devices", newDevice, DeviceDTO.class);
            System.out.println("Dispositivo registrado correctamente.");
        } catch (Exception e) {
            System.out.println("ℹEl dispositivo ya existe o el servidor no está disponible.");
        }

        // 2. Bucle de envío de datos sintéticos [cite: 29, 54]
        System.out.println("Iniciando simulador REST...");
        while (true) {
            // Generar temperatura (15-30) y humedad (30-80) [cite: 67, 72]
            double temp = 15 + (30 - 15) * random.nextDouble();
            double hum = 30 + (80 - 30) * random.nextDouble();

            MetricDTO metric = new MetricDTO(DEVICE_ID, temp, hum);

            try {
                // Enviar métrica al endpoint de ingesta [cite: 91]
                restTemplate.postForLocation(SERVER_URL + "/metrics/ingest", metric);
                System.out.printf("[REST] Enviado -> Temp: %.2fºC | Hum: %.2f%%\n", temp, hum);
            } catch (Exception e) {
                System.err.println("Error al enviar datos: " + e.getMessage());
            }

            Thread.sleep(5000); // Envío periódico cada 5 segundos
        }
    }

    public record DeviceDTO(String id, String protocol, String room, String department, String floor, String building) {}
    public record MetricDTO(String deviceId, double temperature, double humidity) {}
}