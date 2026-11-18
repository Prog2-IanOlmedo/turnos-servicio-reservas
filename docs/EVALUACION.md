<!-- evaluacion:inicio -->
# Evaluación — Prog2-IanOlmedo/turnos-servicio-reservas

> Documento **generado automáticamente** por la cátedra de Programación II (UM): RepoSense + análisis de calidad de **7 dimensiones**. **No editar a mano**: se regenera en cada evaluación.
> Rama evaluada: `main`. Análisis **heurístico** (regex + métricas) + build: es una criba para orientar la revisión, **no una nota**. La **documentación** del repositorio no se evalúa.
> Generado: 2026-11-18.

## 1. Veredicto

| Aspecto | Resultado |
|---|---|
| Índice de calidad | **77/100** |
| Tipo de repositorio | `backend` |
| Build (`mvnw verify` / `gradlew check`) | OK |
| Código (LOC) | 5,037 |
| Archivos de código | 157 |
| Clases | 158 |
| Código dentro de clases (%POO) | 64% |
| Funciones fuera de clases | 0 |
| Duplicación (puerta) | 0.0% |
| Duplicación lógica | 0.0% |
| Duplicación de boilerplate | 4.8% |
| Cobertura | 97% |
| Tope duro aplicado a D1 | `POO_BAJO` → D1 ≤ 60 |

> Este informe **describe** lo que el análisis midió con los criterios ya establecidos de la materia. No es una nota.

## 2. Semáforo de aprobación

Criterios de la cátedra (`CRITERIOS-CALIDAD.md`): **Aprobar** se juega con el perfil `estandar`; **destacar**, con el `exigente` (meta de excelencia).

| Semáforo | Resultado |
|---|---|
| Aprobar (`estandar`) | **cumple** — 8/11 |
| Destacar (`exigente`) | **no cumple** — 6/11 |

**Para destacar, falta cumplir:** SOLID; POO

## 3. Autoría (RepoSense)

| Autor | Commits | Código (LOC) | Tests (LOC) | Docs (LOC) | Días activos | % del código |
|---|---:|---:|---:|---:|---:|---:|
| IanOlmedo | 44 | 3,034 | 3,075 | 135 | 16 | 100% |

> _El LOC de autoría lo calcula RepoSense sobre el historial y puede diferir del LOC de código del análisis (que mira el estado de `main`)._

## 4. Dimensiones evaluadas

Cada dimensión indica **qué mide**, **por qué importa** y **tu resultado**. Cuando el puntaje queda por debajo de la referencia, se agregan preguntas para **investigar la causa** (no hay recetas).

| Dimensión | Peso | Puntaje |
|:--|--:|--:|
| **D1** Diseño OO, SOLID y patrones (20%) | 20% | 7 |
| **D2** Arquitectura hexagonal — backend (15%) | 15% | 100 |
| **D3** Frontend KMP: MVVM y buenas prácticas (10%) | 10% | N/A |
| **D4** Integración y contrato (10%) | 10% | 80 |
| **D5** Seguridad y robustez (10%) | 10% | 100 |
| **D6** Pruebas y cobertura (20%) | 20% | 100 |
| **D7** Proceso y entrega (15%) | 15% | 100 |

### D1. Diseño OO, SOLID y patrones (20%)

- **Qué mide:** Métricas orientadas a objetos (clases, métodos, campos, cohesión, acoplamiento, herencia) y proxies de SOLID, más patrones de diseño.
- **Por qué importa:** El eje de la materia es el buen uso de la POO y su arquitectura: un diseño con clases claras y poco acopladas es más fácil de entender, probar y cambiar.
- **Tu resultado:** **7/100**
- **Para investigar la causa:**
    - ¿Se pensó el modelo de clases antes de codificar o se agrupó después?
    - ¿Qué clases concentran muchas responsabilidades a la vez?
    - ¿Los colaboradores llegan como parámetros o se crean por dentro?

### D2. Arquitectura hexagonal — backend (15%)

- **Qué mide:** Separación en capas (dominio, puertos de entrada/salida, adaptadores) y dirección de las dependencias del backend.
- **Por qué importa:** Aislar el dominio de la infraestructura permite cambiar la base de datos, el framework o el transporte sin tocar las reglas de negocio.
- **Tu resultado:** **100/100**

### D4. Integración y contrato (10%)

- **Qué mide:** Cumplimiento del contrato de integración de la cátedra (formato de errores, paginación, autenticación, endpoints esperados).
- **Por qué importa:** Los servicios deben poder integrarse entre sí y con la app sin sorpresas; el contrato es lo que hacen cumplir las pruebas de integración.
- **Tu resultado:** **80/100**

### D5. Seguridad y robustez (10%)

- **Qué mide:** Credenciales embebidas, consultas SQL por concatenación, y presencia de config de entorno; aislamiento de secretos.
- **Por qué importa:** Un secreto en el repositorio o una consulta construida por concatenación son riesgos concretos, no hipotéticos.
- **Tu resultado:** **100/100**

### D6. Pruebas y cobertura (20%)

- **Qué mide:** Cantidad y resultado de pruebas y cobertura de línea (JaCoCo/Kover, Surefire/Failsafe) cuando hay build.
- **Por qué importa:** Las pruebas son la red de seguridad para refactorizar e integrar; la cobertura acota las zonas sin verificar.
- **Tu resultado:** **100/100**

### D7. Proceso y entrega (15%)

- **Qué mide:** Artefactos verificables de proceso/entrega: `compose`, `.env.example`, CI (`.github/workflows/`), `CODEOWNERS` y wrapper de build (`mvnw`/`gradlew`). **No** puntúa documentación.
- **Por qué importa:** Un proyecto que compila, se levanta y se valida solo es más fácil de revisar y de integrar en equipo.
- **Tu resultado:** **100/100**

## 5. Señales detectadas

El análisis encontró las siguientes señales, con el archivo y la línea donde se originan cuando están disponibles:

**Archivos con más señales**

| Archivo | Señales |
|---|---:|
| `src/main/java/com/turnos/reservas/reservation/domain/model/ReservationProcess.java` | 4 |
| `src/main/java/com/turnos/reservas/reservation/infrastructure/client/CatedraAppointmentClient.java` | 3 |
| `src/main/java/com/turnos/reservas/reservation/application/service/ReservationService.java` | 3 |
| `src/main/java/com/turnos/reservas/shared/web/GlobalExceptionHandler.java` | 3 |
| `src/main/java/com/turnos/reservas/reservation/infrastructure/messaging/KafkaPhoneSubmissionPublisher.java` | 2 |
| `src/main/java/com/turnos/reservas/reservation/infrastructure/client/CatalogScheduleClient.java` | 2 |
| `src/main/java/com/turnos/reservas/reservation/infrastructure/client/BearerTokenPropagationInterceptor.java` | 2 |
| `src/main/java/com/turnos/reservas/reservation/infrastructure/client/mapper/CatedraAppointmentMapper.java` | 2 |
| `src/main/java/com/turnos/reservas/reservation/application/usecases/SubmitPhoneUseCaseImpl.java` | 2 |
| `src/main/java/com/turnos/reservas/reservation/domain/model/ReservationEvent.java` | 2 |

**DIP/concretos** (33)
- clase 'ReservationService' muy acoplada (CBO=17) — `src/main/java/com/turnos/reservas/reservation/application/service/ReservationService.java:24`
- clase 'ReconcileOverdueProcessesUseCaseImpl' muy acoplada (CBO=9) — `src/main/java/com/turnos/reservas/reservation/application/usecases/ReconcileOverdueProcessesUseCaseImpl.java:25`
- clase 'StartReservationUseCaseImpl' muy acoplada (CBO=12) — `src/main/java/com/turnos/reservas/reservation/application/usecases/StartReservationUseCaseImpl.java:22`
- clase 'SubmitPhoneUseCaseImpl' muy acoplada (CBO=13) — `src/main/java/com/turnos/reservas/reservation/application/usecases/SubmitPhoneUseCaseImpl.java:18`
- clase 'SubmitPhoneUseCaseImpl' instancia 3 concretos (CBO=13) — `src/main/java/com/turnos/reservas/reservation/application/usecases/SubmitPhoneUseCaseImpl.java:18`
- clase 'ReservationEvent' muy acoplada (CBO=12) — `src/main/java/com/turnos/reservas/reservation/domain/model/ReservationEvent.java:11`
- clase 'ReservationEventType' muy acoplada (CBO=13) — `src/main/java/com/turnos/reservas/reservation/domain/model/ReservationEventType.java:7`
- clase 'ReservationProcess' muy acoplada (CBO=29) — `src/main/java/com/turnos/reservas/reservation/domain/model/ReservationProcess.java:14`
- clase 'AppointmentGateway' muy acoplada (CBO=8) — `src/main/java/com/turnos/reservas/reservation/domain/ports/out/AppointmentGateway.java:13`
- clase 'BearerTokenPropagationInterceptor' muy acoplada (CBO=8) — `src/main/java/com/turnos/reservas/reservation/infrastructure/client/BearerTokenPropagationInterceptor.java:15`
- clase 'CatalogScheduleClient' muy acoplada (CBO=15) — `src/main/java/com/turnos/reservas/reservation/infrastructure/client/CatalogScheduleClient.java:21`
- clase 'CatalogScheduleClient' instancia 3 concretos (CBO=15) — `src/main/java/com/turnos/reservas/reservation/infrastructure/client/CatalogScheduleClient.java:21`
- clase 'CatedraAppointmentClient' muy acoplada (CBO=33) — `src/main/java/com/turnos/reservas/reservation/infrastructure/client/CatedraAppointmentClient.java:37`
- clase 'CatedraAppointmentClient' instancia 6 concretos (CBO=33) — `src/main/java/com/turnos/reservas/reservation/infrastructure/client/CatedraAppointmentClient.java:37`
- clase 'CatedraAppointmentMapper' muy acoplada (CBO=11) — `src/main/java/com/turnos/reservas/reservation/infrastructure/client/mapper/CatedraAppointmentMapper.java:18`
- clase 'CatedraAppointmentMapper' instancia 3 concretos (CBO=11) — `src/main/java/com/turnos/reservas/reservation/infrastructure/client/mapper/CatedraAppointmentMapper.java:18`
- clase 'CatedraReservationEventListener' muy acoplada (CBO=8) — `src/main/java/com/turnos/reservas/reservation/infrastructure/messaging/CatedraReservationEventListener.java:21`
- clase 'KafkaPhoneSubmissionPublisher' muy acoplada (CBO=17) — `src/main/java/com/turnos/reservas/reservation/infrastructure/messaging/KafkaPhoneSubmissionPublisher.java:25`
- clase 'KafkaPhoneSubmissionPublisher' instancia 3 concretos (CBO=17) — `src/main/java/com/turnos/reservas/reservation/infrastructure/messaging/KafkaPhoneSubmissionPublisher.java:25`
- clase 'ReservationProcessEntity' muy acoplada (CBO=8) — `src/main/java/com/turnos/reservas/reservation/infrastructure/persistence/entity/ReservationProcessEntity.java:25`
- clase 'AvailabilityController' muy acoplada (CBO=15) — `src/main/java/com/turnos/reservas/reservation/infrastructure/web/controller/AvailabilityController.java:24`
- clase 'ReservationController' muy acoplada (CBO=11) — `src/main/java/com/turnos/reservas/reservation/infrastructure/web/controller/ReservationController.java:28`
- clase 'ReservationExceptionHandler' muy acoplada (CBO=27) — `src/main/java/com/turnos/reservas/reservation/infrastructure/web/controller/ReservationExceptionHandler.java:22`
- clase 'GlobalExceptionHandler' muy acoplada (CBO=14) — `src/main/java/com/turnos/reservas/shared/web/GlobalExceptionHandler.java:18`
- clase 'AuthenticateUserUseCaseImpl' muy acoplada (CBO=9) — `src/main/java/com/turnos/reservas/user/application/usecases/AuthenticateUserUseCaseImpl.java:16`
- clase 'RegisterUserUseCaseImpl' muy acoplada (CBO=9) — `src/main/java/com/turnos/reservas/user/application/usecases/RegisterUserUseCaseImpl.java:18`
- clase 'JwtConfig' muy acoplada (CBO=18) — `src/main/java/com/turnos/reservas/user/infrastructure/security/JwtConfig.java:24`
- clase 'JwtConfig' instancia 5 concretos (CBO=18) — `src/main/java/com/turnos/reservas/user/infrastructure/security/JwtConfig.java:24`
- clase 'JwtTokenIssuer' muy acoplada (CBO=10) — `src/main/java/com/turnos/reservas/user/infrastructure/security/JwtTokenIssuer.java:18`
- clase 'SecurityConfig' muy acoplada (CBO=33) — `src/main/java/com/turnos/reservas/user/infrastructure/security/SecurityConfig.java:22`
- clase 'SecurityConfig' instancia 4 concretos (CBO=33) — `src/main/java/com/turnos/reservas/user/infrastructure/security/SecurityConfig.java:22`
- clase 'AccountController' muy acoplada (CBO=17) — `src/main/java/com/turnos/reservas/user/infrastructure/web/controller/AccountController.java:26`
- clase 'UserExceptionHandler' muy acoplada (CBO=15) — `src/main/java/com/turnos/reservas/user/infrastructure/web/controller/UserExceptionHandler.java:19`

**SRP/responsabilidad** (13)
- clase 'ReservationService' grande (LOC=40, métodos=8, campos=8) — `src/main/java/com/turnos/reservas/reservation/application/service/ReservationService.java:24`
- clase 'ReservationService' con baja cohesión (0%) — `src/main/java/com/turnos/reservas/reservation/application/service/ReservationService.java:24`
- método 'PhoneSubmission.PhoneSubmission' con 6 parámetros — `src/main/java/com/turnos/reservas/reservation/domain/model/PhoneSubmission.java:7`
- método 'ReservationEvent.applyTo' con complejidad 7 — `src/main/java/com/turnos/reservas/reservation/domain/model/ReservationEvent.java:21`
- clase 'ReservationProcess' grande (LOC=134, métodos=16, campos=12) — `src/main/java/com/turnos/reservas/reservation/domain/model/ReservationProcess.java:14`
- clase 'ReservationProcess' con baja cohesión (21%) — `src/main/java/com/turnos/reservas/reservation/domain/model/ReservationProcess.java:14`
- método 'ReservationProcess.reconcileWith' con complejidad 6 — `src/main/java/com/turnos/reservas/reservation/domain/model/ReservationProcess.java:134`
- método 'ReservationRequest.ReservationRequest' con 6 parámetros — `src/main/java/com/turnos/reservas/reservation/domain/model/ReservationRequest.java:8`
- clase 'CatedraAppointmentClient' con baja cohesión (36%) — `src/main/java/com/turnos/reservas/reservation/infrastructure/client/CatedraAppointmentClient.java:37`
- método 'CatedraReservationEventDto.CatedraReservationEventDto' con 6 parámetros — `src/main/java/com/turnos/reservas/reservation/infrastructure/messaging/dto/CatedraReservationEventDto.java:10`
- método 'PhoneSubmittedEventDto.PhoneSubmittedEventDto' con 9 parámetros — `src/main/java/com/turnos/reservas/reservation/infrastructure/messaging/dto/PhoneSubmittedEventDto.java:5`
- método 'GlobalExceptionHandler.handleExceptionInternal' con 5 parámetros — `src/main/java/com/turnos/reservas/shared/web/GlobalExceptionHandler.java:30`
- método 'UserRegistration.UserRegistration' con 7 parámetros — `src/main/java/com/turnos/reservas/user/domain/model/UserRegistration.java:7`

**OCP/type-switch** (2)
- clase 'BearerTokenPropagationInterceptor' con 1 type-checks — `src/main/java/com/turnos/reservas/reservation/infrastructure/client/BearerTokenPropagationInterceptor.java:15`
- clase 'GlobalExceptionHandler' con 1 type-checks — `src/main/java/com/turnos/reservas/shared/web/GlobalExceptionHandler.java:18`

## 6. Evolución por commit

Métricas calculadas sobre el estado de `main` en **cada commit** (16 commits con trabajo), del primero al último. La calidad usa el perfil `exigente`; el aporte de código lo mide RepoSense. El índice puede **bajar** al crecer el proyecto: se acumulan señales.

```mermaid
xychart-beta
    title "Índice de calidad a lo largo de los commits"
    x-axis ["4bff92a", "4c5525c", "93056c6", "6301db6", "d82f2f0", "18443c4", "ecee5a2", "c0b966d", "df190cf", "76ef317", "476f48e", "360b673", "1973401", "c4b62a1", "2ef0726", "c395f52"]
    y-axis "Índice" 0 --> 100
    line [0, 15, 42, 47, 44, 49, 55, 61, 61, 61, 61, 67, 66, 66, 66, 66]
```

| # | Fecha | Commit | Autor | LOC | Índice | %POO | Duplic. | Sueltas |
|---|---|---|---|---:|---:|---:|---:|---:|
| 1 | 2026-09-05 | `4bff92a` | IanOlmedo | 0 | 0 | 0% | 0.0% | 0 |
| 2 | 2026-09-10 | `4c5525c` | Ing. Daniel Quinteros | 0 | 15 | 0% | 0.0% | 0 |
| 3 | 2026-10-13 | `93056c6` | IanOlmedo | 41 | 42 | 56% | 0.0% | 0 |
| 4 | 2026-10-17 | `6301db6` | Ing. Daniel Quinteros | 41 | 47 | 56% | 0.0% | 0 |
| 5 | 2026-10-22 | `d82f2f0` | IanOlmedo | 199 | 44 | 83% | 0.0% | 0 |
| 6 | 2026-10-24 | `18443c4` | IanOlmedo | 371 | 49 | 73% | 0.0% | 0 |
| 7 | 2026-10-24 | `ecee5a2` | IanOlmedo | 656 | 55 | 70% | 0.0% | 0 |
| 8 | 2026-10-30 | `c0b966d` | IanOlmedo | 1,116 | 61 | 65% | 2.6% | 0 |
| 9 | 2026-11-02 | `df190cf` | IanOlmedo | 1,116 | 61 | 65% | 2.6% | 0 |
| 10 | 2026-11-02 | `76ef317` | IanOlmedo | 1,544 | 61 | 63% | 1.9% | 0 |
| 11 | 2026-11-04 | `476f48e` | IanOlmedo | 2,094 | 61 | 64% | 1.4% | 0 |
| 12 | 2026-11-05 | `360b673` | IanOlmedo | 3,334 | 67 | 64% | 0.9% | 0 |
| 13 | 2026-11-10 | `1973401` | IanOlmedo | 4,056 | 66 | 65% | 0.0% | 0 |
| 14 | 2026-11-11 | `c4b62a1` | IanOlmedo | 4,521 | 66 | 65% | 0.0% | 0 |
| 15 | 2026-11-17 | `2ef0726` | IanOlmedo | 5,037 | 66 | 64% | 0.0% | 0 |
| 16 | 2026-11-17 | `c395f52` | IanOlmedo | 5,037 | 66 | 64% | 0.0% | 0 |

---
_Informe generado por la cátedra. Las dimensiones y umbrales corresponden a `calidad.py` (perfil `exigente`); el semáforo, a `CRITERIOS-CALIDAD.md`. La documentación del repositorio no se evalúa. Ante dudas, revisá el archivo señalado._
<!-- evaluacion:fin -->
