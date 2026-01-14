package com.uevora.sd.service;

import com.uevora.sd.model.Device;
import com.uevora.sd.model.Metric;
import com.uevora.sd.repository.DeviceRepository;
import com.uevora.sd.repository.MetricRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/*
  Servicio principal de la logica de negocio.
 Acftua como un punto centralizado donde unen los datos de todos los protocolos
 (REST, gRPC y MQTT) para ser validados y almacenados de forma uniforme.
 */
@Service
public class DataProcessingService {

    // Repositorio para consultar si los dispositivos existen y estan activos
    @Autowired
    private DeviceRepository deviceRepository;

    // Repositorio para guardar las metricas validas en la base de datos
    @Autowired
    private MetricRepository metricRepository;

    /**
     * Metodo unico para procesar metricas, independientemente de su origen.
     *
     * @param deviceId Identificador del sensor.
     * @param temperature Valor de la temperatura.
     * @param humidity Valor de la humedad.
     * @param timestamp Momento en que se recibio el dato.
     * @return true si se guardo correctamente, false si hubo un error de validacion.
     */
    public boolean processMetric(String deviceId, double temperature, double humidity, LocalDateTime timestamp) {

        // 1. Validacion de existencia (Requisito: Solo aceptar datos de sensores registrados)
        Optional<Device> deviceOpt = deviceRepository.findById(deviceId);

        if (deviceOpt.isEmpty()) {
            // Mensaje de alerta en portugues para la consola del servidor
            System.out.println("ALERTA: Metrica descartada. Dispositivo nao registado: " + deviceId);
            return false; // Rechazamos la metrica
        }

        Device device = deviceOpt.get();

        // 2. Validacion de estado (Requisito: El sensor debe estar marcado como activo)
        if (!device.isActive()) {
            System.out.println("ALERTA: Metrica descartada. Dispositivo inativo: " + deviceId);
            return false;
        }

        // 3. Persistencia de datos
        // Si pasa las validaciones, creamos el objeto entidad y lo guardamos en PostgreSQL
        Metric metric = new Metric(deviceId, temperature, humidity, timestamp);
        metricRepository.save(metric);

        // Confirmacion en consola
        System.out.println("Metrica guardada para " + deviceId + ": " + temperature + "C, " + humidity + "%");
        return true;
    }
}