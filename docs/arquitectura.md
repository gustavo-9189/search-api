# Arquitectura del proyecto — Search API

## Descripción general

API REST construida con **Spring Boot 3.5 / Java 21** que implementa un servicio de búsqueda de disponibilidad hotelera. Usa **arquitectura hexagonal** (ports & adapters), persistencia en **Oracle DB**, mensajería asíncrona con **Kafka**, y **virtual threads** para las operaciones de escritura.

---

## Estructura de capas

```
com.search_api/
├── domain/                         <- Núcleo del negocio, sin dependencias de Spring
│   ├── model/                      <- Search, SearchCount (Java records)
│   ├── exception/                  <- SearchNotFoundException
│   └── port/
│       ├── in/                     <- CreateSearchUseCase, CountSearchUseCase (interfaces)
│       └── out/                    <- SearchRepository, SearchEventPublisher (interfaces)
│
├── application/
│   └── service/                    <- CreateSearchService, CountSearchService
│
└── infrastructure/
    ├── adapter/
    │   ├── in/
    │   │   ├── rest/               <- SearchController, DTOs, validador @ValidSearchDates
    │   │   └── kafka/              <- SearchKafkaConsumer
    │   └── out/
    │       ├── kafka/              <- SearchKafkaProducer
    │       └── persistence/        <- SearchPersistenceAdapter, SearchJpaRepository, SearchEntity
    ├── config/                     <- ApplicationConfig, KafkaConfig, OpenApiConfig
    └── exception/                  <- GlobalExceptionHandler
```

---

## Flujo POST /search

```
Cliente
  │
  ▼
SearchController          valida SearchRequest con @Valid + @ValidSearchDates
  │
  ▼
CreateSearchService       genera UUID searchId, construye record Search
  │
  ▼
SearchKafkaProducer       publica en topic "hotel_availability_searches"
  │
  ▼
[respuesta inmediata]     devuelve {"searchId": "uuid"}  ← no espera persistencia

         (asíncrono)
              │
              ▼
SearchKafkaConsumer       recibe el mensaje (corre en virtual thread)
              │
              ▼
SearchPersistenceAdapter  serializa ages a JSON y guarda en tabla SEARCHES (Oracle)
```

---

## Flujo GET /count?searchId=

```
Cliente
  │
  ▼
SearchController
  │
  ▼
CountSearchService
  ├── findBySearchId(searchId)          → lanza SearchNotFoundException si no existe
  └── countBySearchFields(search)       → cuenta filas con mismo hotelId + checkIn
                                           + checkOut + ages (orden exacto)
  │
  ▼
[respuesta]  { searchId, search: { hotelId, checkIn, checkOut, ages }, count }
```

---

## Decisiones de diseño clave

### Persistencia asíncrona via Kafka
El `POST /search` retorna el `searchId` inmediatamente sin esperar a que el dato llegue a la base de datos. La persistencia ocurre cuando el `SearchKafkaConsumer` procesa el mensaje del topic.

### Virtual threads
Configurados a nivel global (`spring.threads.virtual.enabled=true`) y explícitamente en el `KafkaConfig` con un `VirtualThreadTaskExecutor` sobre el listener container factory. Permite alta concurrencia sin bloqueo en los consumers.

### Ages como JSON string
El campo `ages` se almacena como string JSON (ej: `[30,29,1]`) en la columna `AGES` de Oracle. La igualdad es **exacta y sensible al orden**: `[30,29,1]` ≠ `[29,30,1]`. La serialización/deserialización la maneja `SearchPersistenceAdapter` con Jackson.

### Formato de fechas
`dd/MM/yyyy` en request y response, gestionado con `@JsonFormat(pattern = "dd/MM/yyyy")` en los records del dominio y DTOs.

### GlobalExceptionHandler
Devuelve `ProblemDetail` (RFC 9457) para errores de validación (400) y `SearchNotFoundException` (404).

---

## Tabla de base de datos

**Tabla:** `SEARCHES`

| Columna | Tipo | Detalle |
|---|---|---|
| `SEARCH_ID` | VARCHAR(36) | PK — UUID generado en `CreateSearchService` |
| `HOTEL_ID` | VARCHAR(100) | Identificador del hotel |
| `CHECK_IN` | DATE | Fecha de entrada |
| `CHECK_OUT` | DATE | Fecha de salida |
| `AGES` | VARCHAR(500) | Edades serializadas como JSON array |

---

## Infraestructura

| Componente | Tecnología | Config por defecto |
|---|---|---|
| Base de datos | Oracle DB | `localhost:1521/FREEPDB1` |
| Mensajería | Apache Kafka | `localhost:9092` |
| Topic Kafka | — | `hotel_availability_searches` |
| Puerto HTTP | — | `8080` |
| Swagger UI | springdoc-openapi | `http://localhost:8080/swagger-ui.html` |

Variables de entorno: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `KAFKA_BOOTSTRAP_SERVERS`.

---

## Testing

| Tipo | Herramienta | Notas |
|---|---|---|
| Unitario | JUnit 5 + Mockito | Sin contexto Spring — servicios, adapters, producer, consumer |
| Slice web | `@WebMvcTest` + MockMvc | Solo `SearchControllerTest` |
| Integración | `@SpringBootTest` + `@EmbeddedKafka` | `SearchApiApplicationTests` |
| BD en tests | H2 (in-memory) | `src/test/resources/application.yaml` — sin Oracle externo |
| Cobertura | JaCoCo | Mínimo 80% en líneas, ramas, métodos e instrucciones (`./mvnw verify`) |