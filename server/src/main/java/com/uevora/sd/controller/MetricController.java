package com.uevora.sd.controller;

import com.uevora.sd.model.Metric;
import com.uevora.sd.repository.MetricRepository;
import com.uevora.sd.service.DataProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 *Controlador REST que expone endpoints para la gestion de metricas via HTTP
 * Permite a los clientes REST enviar datos y a la consola de administracion leerlos.
 */
@RestController
@RequestMapping("/api/metrics")
public class MetricController {

    // Servicio de logica de negocio para validar y procesar datos
    @Autowired
    private DataProcessingService dataProcessingService;

    // Repositorio para acceder a la base de datos (necesario para las consultas de lectura)
    @Autowired
    private MetricRepository metricRepository;

    /*
     Endpoint POST para recibir metricas.
     Es utilizado por los sensores que usan el protocolo REST (HTTP)
     @param metric Objeto JSON recibido en el cuerpo de la peticion y convertido a Java
     @return ResponseEntity con el stado de la operacion y un mensaje.
     */
    @PostMapping
    public ResponseEntity<String> receiveMetric(@RequestBody Metric metric) {
        // Delegamos el procesamiento al servicio principal
        boolean success = dataProcessingService.processMetric(
                metric.getDeviceId(),
                metric.getTemperature(),
                metric.getHumidity(),
                metric.getTimestamp()
        );

        if (success) {
            // Retorna codigo 200 OK
            return ResponseEntity.ok("Metrica processada com sucesso");
        } else {
            // Retorna codigo 400 Bad Request si el dispositivo no existe o esta inactivo
            return ResponseEntity.badRequest().body("Erro: Dispositivo nao registado ou inativo");
        }
    }

    /*
     Endpoint GET para obtener el historial completo de metricas.
     Este metodo es para que (CLI) pueda leer.
     *@return Una lista JSON con todas las metricas almacenadas en la base de datos.
     */
    @GetMapping
    public List<Metric> getAllMetrics() {
        // Utiliza el repositorio JPA para hacer un 'SELECT * FROM metrics'
        return metricRepository.findAll();
    }
}