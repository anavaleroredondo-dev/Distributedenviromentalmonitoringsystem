package com.uevora.sd.grpc;

import com.uevora.sd.proto.MetricRequest;
import com.uevora.sd.proto.MetricResponse;
import com.uevora.sd.proto.MetricServiceGrpc;
import com.uevora.sd.service.DataProcessingService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;

/**
 * Implementacion del servicio gRPC definido en el archivo .proto.
 * Esta clase actua como el servidor que escucha en el puerto 9090.
 */
// @GrpcService:Indica a Spring Boot que esta clase debe exponerse como un servidor gRPC. Si esta en rojo, falta la dependencia 'grpc-server-spring-boot-starter'.
@GrpcService
public class MetricGrpcService extends MetricServiceGrpc.MetricServiceImplBase {

    // Inyeccion de dependencia del servicio que procesa la logica de negocio
    // @Autowired: Permite a Spring instanciar y conectar automaticamente el servicio.
    @Autowired
    private DataProcessingService dataProcessingService;


     //Metodo que se ejecuta cuando un cliente gRPC llama a 'SendMetric'.
    @Override
    public void sendMetric(MetricRequest request, StreamObserver<MetricResponse> responseObserver) {
        // Mensaje en consola para trazabilidad (en portugues)
        System.out.println("[Servidor gRPC] Pedido recebido do ID: " + request.getDeviceId());

        // Llamamos al servicio de procesamiento de datos (el mismo que usa REST y MQTT)
        boolean success = dataProcessingService.processMetric(
                request.getDeviceId(),
                request.getTemperature(),
                request.getHumidity(),
                LocalDateTime.now()
        );

        // Construimos la respuesta definida en el .proto
        MetricResponse response = MetricResponse.newBuilder()
                .setSuccess(success)
                .setMessage(success ? "DADOS GUARDADOS" : "ERRO: Dispositivo nao registado")
                .build();

        // Enviamos la respuesta al cliente (onNext)
        responseObserver.onNext(response);

        // Cerramos la conexion de esta llamada (onCompleted)
        responseObserver.onCompleted();
    }
}