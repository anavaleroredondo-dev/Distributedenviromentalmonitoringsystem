package com.uevora.sd.config;

import com.uevora.sd.service.DataProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import java.time.LocalDateTime;
import java.util.Map;

/*
 * Clase de configuraccion para la integrcion con MQTT usando Spring Integration.
 * Define como se reciben y procesan los mensaes desde el Broker (Mosquitto).
 */
@Configuration
public class MqttConfig {

    /*
     * Define el canal de entrada (Input Channel).
     * Actua como un puente o tuberia interna por donde pasan los mensajes
     * desde el adaptador MQTT hasta el manejador de la logica de negocio.
     */
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    /*
     * Configuracion del adaptador de entrada MQTT.
     * Este componente se conecta al Broker Mosquitto y se suscribe a los topicos.
     */
    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                "tcp://localhost:1883",          // URL del Broker local
                "server-subscriber-client",      // ID unico de este cliente
                "sensores/+/metricas");          // Topico con comodin (+) para escuchar a todos los sensores

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1); // Calidad de servicio 1 (At least once)

        // Conectamos el adaptador al canal definido anteriormente
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }


     //Manejador de mensajes (Service Activator).
     //Escucha el canal de entrada, recibe el mensaje, lo deserializa de JSON a un Mapa
      //y llama al servicio de procesamiento de datos.
     /*
     * @param service Servicio de logica de negocio inyectado por Spring.
     * @return El manejador de mensajes.
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler(DataProcessingService service) {
        return message -> {
            try {
                // Obtenemos el cuerpo del mensaje (payload)
                String payload = message.getPayload().toString();

                // Usamos JSON para convertir el String JSON a un objeto Map
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> data = mapper.readValue(payload, Map.class);

                // Llamamos al servicio central para guardar el dato en la base de datos
                service.processMetric(
                        (String) data.get("deviceId"),
                        (Double) data.get("temperature"),
                        (Double) data.get("humidity"),
                        LocalDateTime.now()
                );
            } catch (Exception e) {
                // Mensaje de error para la consola
                System.err.println("Erro ao processar mensagem MQTT: " + e.getMessage());
            }
        };
    }
}