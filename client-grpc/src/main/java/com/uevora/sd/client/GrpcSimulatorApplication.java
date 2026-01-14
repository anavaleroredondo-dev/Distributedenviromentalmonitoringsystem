package com.uevora.sd.client;

import com.uevora.sd.proto.MetricRequest;
import com.uevora.sd.proto.MetricResponse;
import com.uevora.sd.proto.MetricServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Random;

//Clase principal del cliente que simula un sensor enviando datos via gRPC.

@SpringBootApplication
public class GrpcSimulatorApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(GrpcSimulatorApplication.class, args);
    }

 //Metodo que se ejecuta al iniciar la aplicacion. Contiene el bucle principal de simulacion.

    @Override
    public void run(String... args) throws Exception {
        // Mensajes de log iniciales en portugues
        System.out.println("Iniciando Cliente gRPC...");
        System.out.println("A tentar conectar a 127.0.0.1:9090...");

        // 1. CONFIGURACION DE LA CONEXION (CHANNEL)
        // Creamos el canal de comunicacion. Usamos la IP 127.0.0.1 para evitar
        // retardos de resolucion DNS en algunos sistemas operativos.
        // usePlaintext(): Importante para desarrollo, indica que no uso encriptacion SSL/TLS.
        ManagedChannel channel = ManagedChannelBuilder.forAddress("127.0.0.1", 9090)
                .usePlaintext()
                .build();

        // 2. CREAR EL CLIENTE (STUB)
        // El "BlockingStub" es un cliente sincrono: el codigo se detiene y espera
        // hasta recibir la respuesta del servidor.
        MetricServiceGrpc.MetricServiceBlockingStub stub = MetricServiceGrpc.newBlockingStub(channel);

        Random random = new Random();
        // ID del dispositivo (debe coincidir con el registrado en la base de datos)
        String deviceId = "sensor-grpc-01";

        // 3. BUCLE INFINITO DE ENVIO
        while (true) {
            // Generacion de datos aleatorios para simular lecturas reales
            double temp = 15 + (30 - 15) * random.nextDouble();
            double hum = 30 + (80 - 30) * random.nextDouble();

            try {
                // Construccion del mensaje Protobuf (Request) usando el patron Builder
                MetricRequest request = MetricRequest.newBuilder()
                        .setDeviceId(deviceId)
                        .setTemperature(temp)
                        .setHumidity(hum)
                        .build();

                // Llamada remota al servidor (RPC) y recepcion de respuesta
                MetricResponse response = stub.sendMetric(request);

                // Imprimimos el resultado en consola (formato portugues)
                System.out.printf("[gRPC] Enviado: %.2fC | Resposta do Servidor: %s%n", temp, response.getMessage());

            } catch (Exception e) {
                // Gestion de errores de conexion
                System.err.println("ERRO DE CONEXAO: " + e.getMessage());
                System.err.println(" -> Verifique se o Servidor esta a correr na porta 9090");
            }

            // Pausa de 5 segundos entre envios para no saturar la red
            Thread.sleep(5000);
        }
    }
}