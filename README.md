<!-- calidad:inicio -->
![Calidad](https://img.shields.io/badge/Calidad-58%2F100-yellow) ![Aprobar](https://img.shields.io/badge/Aprobar-no-red) ![Destacar](https://img.shields.io/badge/Destacar-no-lightgrey) ![Build](https://img.shields.io/badge/Build-OK-green) ![Duplicación](https://img.shields.io/badge/Duplicaci%C3%B3n-0.0%25-green) ![%POO](https://img.shields.io/badge/%25POO-54%25-orange) ![Sueltas](https://img.shields.io/badge/Sueltas-0-green) ![D1](https://img.shields.io/badge/D1-60-yellow) ![D2](https://img.shields.io/badge/D2-36-red) ![D4](https://img.shields.io/badge/D4-0-red) ![D5](https://img.shields.io/badge/D5-100-green) ![D6](https://img.shields.io/badge/D6-48-orange) ![D7](https://img.shields.io/badge/D7-100-green)

**Calidad (heurístico, 7 dimensiones):** índice **58/100** · build **OK** · aprobar **no (7/11)** · destacar **no**
Detalle completo en [`docs/EVALUACION.md`](docs/EVALUACION.md).
<!-- calidad:fin -->

# Servicio de reservas

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Prog2-IanOlmedo_turnos-servicio-reservas&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Prog2-IanOlmedo_turnos-servicio-reservas) [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Prog2-IanOlmedo_turnos-servicio-reservas&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Prog2-IanOlmedo_turnos-servicio-reservas)

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
