# Система управления спутниковой группировкой

## 1. Общее описание

Backend-система для управления группировками искусственных спутников Земли. Позволяет создавать группировки, добавлять спутники связи и наблюдения, активировать их и выполнять целевые миссии. Реализация на Java 21, Spring Boot, следует принципам SOLID и паттернам GoF.

Три микросервиса:
- **satellite** - REST API управления спутниками и группировками
- **scheduler** - планировщик миссий по cron-расписанию
- **telemetry-service** - эмулятор телеметрии спутников

## 2. Технологии

- Java 21
- Spring Boot 3.2 (Spring MVC, Spring Data JPA, Spring Validation, Spring AOP)
- PostgreSQL 15
- gRPC + Protobuf
- Liquibase
- Lombok, MapStruct
- SpringDoc OpenAPI
- Feign + Outbox Transactional
- Docker / Docker Compose
- JUnit 5, MockMvc, Testcontainers

## 3. Архитектура

### 3.1. satellite

- `common` - общие утилиты, обработка исключений, CRUD-интерфейсы, AOP
- `constellation` - сущность `Constellation`, репозиторий, сервисы, REST-контроллер
- `satellite` - иерархия `Satellite` → `CommunicationSatellite` / `ImagingSatellite`, репозиторий, сервисы, контроллер
- `operations` - фасад `SpaceOperationCenterService`, контроллер миссий
- `telemetry` - gRPC-клиент для приема телеметрии

### 3.2. scheduler

Планировщик, отправляющий миссии в satellite по cron-расписанию через Feign. Гарантия доставки - паттерн Outbox Transactional.

### 3.3. telemetry-service

Эмулирует телеметрию 5 спутников. Каждые 2 секунды генерирует внутреннюю и внешнюю температуру и передает данные через gRPC Server Streaming. Спутник получает обновления в реальном времени и сохраняет температуры в БД.

## 4. Взаимодействие сервисов

```
telemetry-service ──gRPC stream──→ satellite
scheduler ──────────Feign/REST───→ satellite
```

- **gRPC**: telemetry-service стримит `TelemetryUpdate` напрямую в satellite
- **Feign**: scheduler отправляет REST-запросы на выполнение миссий в satellite

## 5. Паттерны проектирования

### 5.1. Порождающие

**Factory Method** - `SatelliteFactory` с реализациями для каждого типа спутника.

**Builder** - `Constellation`, `EnergySystem`, DTO.

### 5.2. Структурные

**Facade** - `SpaceOperationCenterService` объединяет операции в высокоуровневый API.

**Decorator** - `@MeasureExecutionTime` + AOP-аспект для замера времени методов.

### 5.3. Поведенческие

**Strategy** - `SatelliteUpdater` с разными стратегиями обновления.

**Template Method** - `Satellite` задает шаблон поведения, подклассы реализуют `performMission()`.

**Outbox Transactional** - гарантированная доставка сообщений из scheduler.

## 6. Принцип инверсии зависимостей

- Сервисы зависят от интерфейсов, не от реализаций
- `SatelliteFactory` внедряется списком - новый тип спутника добавляется без изменения кода
- Репозитории - интерфейсы, реализации предоставляет Spring Data JPA
- Внедрение через конструктор

## 7. Инициализация данных

При старте satellite создаются две группировки (Орбита-1, Орбита-2) с 5 спутниками и выполняется демонстрационный прогон миссий.

## 8. Сборка и запуск

### 8.1. Docker Compose

```bash
docker-compose up -d --build
```

Поднимает PostgreSQL, satellite (8080), scheduler (8081), telemetry-service (9091 gRPC / 9092 REST).

Swagger UI: `http://localhost:8080/swagger-ui.html`

### 8.2. Локальный запуск

Требует PostgreSQL на localhost.

```bash
cd satellite && ./gradlew bootRun --args='--spring.profiles.active=local'
cd scheduler && ./gradlew bootRun --args='--spring.profiles.active=local'
cd telemetry-service && ./gradlew bootRun
```

### 8.3. Профили Spring

| Профиль | Назначение |
|---|---|
| `local` | БД на localhost |
| `docker` | Адреса во внутренней Docker-сети |
| `test` | Тесты с Testcontainers |

### 8.4. Порты

| Сервис | REST | gRPC |
|---|---|---|
| satellite | 8080 | - |
| scheduler | 8081 | - |
| telemetry-service | 9092 | 9091 |
| PostgreSQL | 5432 | - |

### 8.5. Логи телеметрии

telemetry-service:

```
Связь-1: внутр. 23.5°C / внеш. -89.3°C
Связь-2: внутр. 21.1°C / внеш. 110.7°C
```

satellite:

```
Связь-1 получил телеметрию: внутр. 23.5°C / внеш. -89.3°C
```

## 9. Тесты

```bash
./gradlew test
./gradlew test jacocoTestReport
```

Отчет покрытия: `build/reports/jacoco/test/html/index.html`
