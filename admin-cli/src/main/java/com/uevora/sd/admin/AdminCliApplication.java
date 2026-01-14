package com.uevora.sd.admin;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Scanner;

/**
 * Clase principal de la Consola de Administracion (CLI).
 * Esta aplicacion permite gestionar el sistema (ver dispositivos, registrarlos, ver metricas)
 * conectandose a la API REST del servidor.
 */
@SpringBootApplication
public class AdminCliApplication implements CommandLineRunner {

    // URL base de la API REST del servidor central
    private final String SERVER_URL = "http://localhost:8081/api";

    // Cliente HTTP sincrono de Spring para realizar peticiones (GET, POST)
    private final RestTemplate restTemplate = new RestTemplate();

    // Escaner para leer la entrada del usuario por la terminal
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AdminCliApplication.class);

        // IMPORTANTE: Desactivamos el entorno web (Tomcat).
        // Al ser una aplicacion de consola, no necesita escuchar en ningun puerto (como el 8080).
        // Esto evita conflictos si el servidor u otros servicios ya estan usando puertos.
        app.setWebApplicationType(WebApplicationType.NONE);

        app.run(args);
    }

    /**
     * Metodo principal que se ejecuta al iniciar la aplicacion.
     * Muestra el menu interactivo en bucle.
     */
    @Override
    public void run(String... args) {
        System.out.println("\n\n============================================");
        System.out.println("   CONSOLA DE ADMINISTRACAO - SSDD   ");
        System.out.println("============================================");

        // Bucle infinito para mantener el menu activo
        while (true) {
            System.out.println("\nSelecione uma opcao:");
            System.out.println("1. Listar dispositivos");
            System.out.println("2. Registar novo dispositivo");
            System.out.println("3. Ver ultimas metricas");
            System.out.println("4. Sair");
            System.out.print("> ");

            String opcion = scanner.nextLine();

            try {
                switch (opcion) {
                    case "1":
                        listarDispositivos();
                        break;
                    case "2":
                        registrarDispositivo();
                        break;
                    case "3":
                        verMetricas();
                        break;
                    case "4":
                        System.out.println("Ate logo!");
                        System.exit(0); // Cierra la aplicacion correctamente
                    default:
                        System.out.println("Opcao invalida.");
                }
            } catch (Exception e) {
                // Gestion de errores si el servidor esta caido
                System.out.println("Erro ao conectar com o servidor: " + e.getMessage());
            }
        }
    }

    // Peticion GET para obtener todos los dispositivos registrados
    private void listarDispositivos() {
        String url = SERVER_URL + "/devices";
        String resultado = restTemplate.getForObject(url, String.class);
        System.out.println("\n--- LISTA DE DISPOSITIVOS ---");
        System.out.println(resultado);
    }

    // Peticion POST para registrar un nuevo sensor manualmente
    private void registrarDispositivo() {
        System.out.println("\n--- REGISTAR DISPOSITIVO ---");
        System.out.print("ID do Sensor (ex: sensor-lab-02): ");
        String id = scanner.nextLine();

        System.out.print("Protocolo (REST, GRPC, MQTT): ");
        String protocol = scanner.nextLine();

        System.out.print("Edificio: ");
        String building = scanner.nextLine();

        // Construccion manual del JSON.
        // Asignamos valores por defecto a 'room' y 'department' para simplificar
        String json = String.format(
                "{\"id\":\"%s\",\"protocol\":\"%s\",\"building\":\"%s\",\"room\":\"Admin\",\"department\":\"Admin\",\"floor\":\"1\",\"active\":true}",
                id, protocol, building
        );

        // Configuramos las cabeceras para indicar que enviamos JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(json, headers);

        // Enviamos la peticion al servidor
        restTemplate.postForObject(SERVER_URL + "/devices", request, String.class);
        System.out.println("Dispositivo registado com sucesso!");
    }

    // Peticion GET para ver el historial de datos recibidos
    private void verMetricas() {
        String url = SERVER_URL + "/metrics";
        String resultado = restTemplate.getForObject(url, String.class);
        System.out.println("\n--- ULTIMAS METRICAS ---");
        System.out.println(resultado);
    }
}