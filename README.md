# Servicio de reservas

Backend de turnos (reservas) del proyecto final de Programación 2.
Spring Boot 4.1.1, Java 25 y PostgreSQL 17.

## Requisitos

- Java 25
- Docker y Docker Compose
- Maven no hace falta: se usa el wrapper (`./mvnw`)

## Configuración

```bash
cp .env.example .env
```

Cambiar `RESERVAS_DB_PASSWORD` en `.env`. Ese archivo no se versiona.

## Ejecución

```bash
docker compose up -d
./mvnw spring-boot:run
```

Para verificar: `curl http://localhost:8080/actuator/health` debe responder `UP`.
Si el puerto 8080 está ocupado (por ejemplo por el servicio de catálogo),
usar `SERVER_PORT=8081 ./mvnw spring-boot:run`.

## Pruebas

```bash
./mvnw test
```

Testcontainers levanta su propio PostgreSQL 17, solo hace falta tener Docker corriendo.
