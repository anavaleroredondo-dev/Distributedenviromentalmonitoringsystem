package com.uevora.sd.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Random;

/*
 Clase principal del cliente que simula un sensor IoT enviando datos via MQTT
 Utiliza la libreriac Eclipse Pahod para conectar con el Broker (Mosquitto)
 */
@SpringBootApplication
public class MqttSimulatorApplication implements CommandLineRunner {

    // Direccion del Broker MQTT corriendo localmente en el puerto 1883
    private static final String BROKER_URL = "tcp://localhost:1883";

    // ID unico del dispositivo (debe coincidir con el registrado en la base de datos)
    private static final String DEVICE_ID = "sensor-mqtt-01";

    // Estructura del topico jerarquico donde publicaremos: sensores/{id}/metricas
    // El servidor esta suscrito a "sensores/+/metricas" para leerlos todos.
    private static final String TOPIC = "sensores/" + DEVICE_ID + "/metricas";

    public static void main(String[] args) {
        SpringApplication.run(MqttSimulatorApplication.class, args);
    }

    /*
     Metodo ejecutado al iniciar la aplicacion Spring Boot.
     ontiene la logica de conexion y el bucle de envio de datos.
     */
    @Override
    public void run(String... args) throws Exception {
        // Creamos el cliente MQTT. Usamos un ID de cliente aleatorio para la sesion TCP.
        MqttClient client = new MqttClient(BROKER_URL, MqttClient.generateClientId());

        // Establecemos la conexion con el Broker
        client.connect();

        Random random = new Random();
        // ObjectMapper de Jackson para serializar objetos Java a formato JSON
        ObjectMapper mapper = new ObjectMapper();

        // Mensaje de log inicial en portugues
        System.out.println("Simulador MQTT iniciado. A publicar em: " + TOPIC);

        // Bucle infinito de simulacion
        while (true) {
            // Generacion de valores aleatorios de temperatura y humedad
            double temp = 15 + (30 - 15) * random.nextDouble();
            double hum = 30 + (80 - 30) * random.nextDouble();

            // Creacion del objeto de datos (POJO/Record) para estructurar la informacion
            MetricMsg msg = new MetricMsg(DEVICE_ID, temp, hum);

            // Serializacion: Convertimos el objeto Java a un String JSON
            // Ejemplo: {"deviceId":"sensor-mqtt-01", "temperature":22.5, "humidity":45.0}
            String jsonPayload = mapper.writeValueAsString(msg);

            // Creacion del mensaje MQTT (payload en bytes)
            MqttMessage message = new MqttMessage(jsonPayload.getBytes());

            // Publicacion al topico configurado
            client.publish(TOPIC, message);

            // Log en consola para trazabilidad (en portugues)
            System.out.printf("[MQTT] Publicado: %.2fC, %.2f%%%n", temp, hum);

            // Pausa de 5 segundos antes del siguiente envio
            Thread.sleep(5000);
        }
    }

    // Record (clase inmutable) que define la estructura del mensaje JSON
    public record MetricMsg(String deviceId, double temperature, double humidity) {}
}