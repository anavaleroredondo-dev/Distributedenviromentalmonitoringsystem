# Sistema de Monitorização Ambiental (SD - Trabalho 2)
**Aluno:** 68791

## Requisitos
- Java 21
- Maven
- Docker
- Mosquitto MQTT Broker (porta 1883)

## Instruções de Execução

1. **Base de Dados:**
   `docker start postgres-container`

2. **Servidor Central:**
   No terminal: `cd server && mvn spring-boot:run`
   (O servidor inicia na porta 8081 e gRPC na 9090)

3. **Consola de Administração:**
   No terminal: `cd admin-cli && mvn spring-boot:run`
   *Use esta consola para registar os sensores antes de iniciar os clientes.*

4. **Clientes Simuladores:**
   - MQTT: `cd client-mqtt && mvn spring-boot:run`
   - gRPC: `cd client-grpc && mvn spring-boot:run`
   - REST: `cd client-rest && mvn spring-boot:run`