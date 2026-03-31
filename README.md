# Search API

API REST para búsqueda de disponibilidad hotelera. Construida con Spring Boot 3.5 / Java 21, arquitectura hexagonal, Kafka para persistencia asíncrona y Oracle DB.

---

## Requisitos

**Solo se necesita Docker.** No es necesario tener instalado Java, Maven ni ninguna otra herramienta.

---

## Levantar el stack completo

```bash
docker compose up --build
```

Este comando:
1. Compila la aplicación dentro de un contenedor Docker (build multi-stage)
2. Inicia Kafka (modo KRaft, sin ZooKeeper), Oracle DB y la aplicación

La aplicación estará disponible en `http://localhost:8080` una vez que todos los servicios estén saludables (Oracle tarda ~60 s en el primer arranque).

Para detener y eliminar los contenedores:

```bash
docker compose down
```

---

## Endpoints

### POST /search

Registra una búsqueda de disponibilidad hotelera. Devuelve el `searchId` de inmediato; la persistencia es asíncrona vía Kafka.

**Body de la solicitud:**
```json
{
  "hotelId": "1234aBc",
  "checkIn": "01/04/2026",
  "checkOut": "05/04/2026",
  "ages": [30, 29, 1, 3]
}
```

- Las fechas deben estar en formato `dd/MM/yyyy`.
- `checkIn` debe ser anterior a `checkOut`.
- `ages` debe contener al menos un elemento.

**Respuesta `200 OK`:**
```json
{
  "searchId": "550e8400-e29b-41d4-a716-446655440000"
}
```

---

### GET /count?searchId={searchId}

Devuelve cuántas veces se realizó la misma búsqueda (mismo `hotelId`, `checkIn`, `checkOut` y `ages` en el mismo orden).

**Respuesta `200 OK`:**
```json
{
  "searchId": "550e8400-e29b-41d4-a716-446655440000",
  "search": {
    "hotelId": "1234aBc",
    "checkIn": "01/04/2026",
    "checkOut": "05/04/2026",
    "ages": [30, 29, 1, 3]
  },
  "count": 3
}
```

**Respuesta `404 Not Found`** si el `searchId` no existe.

---

## Swagger / OpenAPI

Una vez que la aplicación esté corriendo, la documentación interactiva de la API está disponible en:

```
http://localhost:8080/swagger-ui/index.html
```

La especificación OpenAPI en formato JSON está en:

```
http://localhost:8080/v3/api-docs
```

---

## Reporte de cobertura (JaCoCo)

Requiere Java 21. Si el `PATH` del sistema apunta a un JDK más antiguo, prefija el comando con el home de Java 21:

```bash
JAVA_HOME="C:/Program Files/Java/jdk-21" ./mvnw verify
```

El reporte HTML se genera en:

```
target/site/jacoco/index.html
```

Abrirlo en un navegador para ver la cobertura de líneas, ramas, métodos e instrucciones. El build exige un **mínimo del 80%** en los cuatro contadores. Si la cobertura cae por debajo de ese umbral, el build falla.

Para correr solo los tests sin verificar cobertura:

```bash
JAVA_HOME="C:/Program Files/Java/jdk-21" ./mvnw test
```

---

## Documentación

- [Arquitectura del proyecto](docs/arquitectura.md) — estructura de capas, flujos de los endpoints y decisiones de diseño.

---

## Variables de entorno

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `DB_URL` | `jdbc:oracle:thin:@localhost:1521/FREEPDB1` | URL JDBC de Oracle |
| `DB_USERNAME` | `searchapi` | Usuario de Oracle |
| `DB_PASSWORD` | `searchapi` | Contraseña de Oracle |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Dirección del broker Kafka |
