# Система управления спутниковой группировкой

## 1. Общее описание

Backend-система для управления группировками искусственных спутников Земли. Позволяет создавать группировки, добавлять спутники связи и наблюдения, активировать их и выполнять целевые миссии. Реализация на Java 21, Spring Boot, следует принципам SOLID и паттернам GoF.

Три микросервиса:
- **satellite** - REST API управления спутниками и группировками
- **scheduler** - планировщик миссий по cron-расписанию
- **telemetry-service** - эмулятор телеметрии

## 2. Технологии

- Java 21
- Spring Boot 3.2 (Spring MVC, Spring Data JPA, Spring Validation, Spring AOP)
- PostgreSQL 15
- Apache Kafka 3.8
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
- `satellite` - иерархия `Satellite` -> `CommunicationSatellite` / `ImagingSatellite`, репозиторий, сервисы, контроллер
- `operations` - фасад `SpaceOperationCenterService`, контроллер миссий
- `telemetry` - Kafka-консьюмер телеметрии

### 3.2. scheduler

Планировщик, отправляющий миссии в satellite по cron-расписанию через Feign. Гарантия доставки - паттерн Outbox Transactional.

### 3.3. telemetry-service

Эмулирует телеметрию 5 спутников. Каждые 2 секунды генерирует внутреннюю и внешнюю температуру, отправляет protobuf-сообщения `TelemetryUpdate` в Kafka topic `telemetry`. Дополнительно доступен gRPC Server Streaming на порту 9091.

## 4. Взаимодействие сервисов

```
telemetry-service - Kafka --> satellite
scheduler - Feign/REST -> satellite
```

- **Kafka**: telemetry-service пишет protobuf-сообщения в topic `telemetry`, satellite читает и обновляет спутники в БД
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

Swagger UI: `http://localhost:8080/swagger-ui.html`

### 8.2. Локальный запуск

Требует PostgreSQL и Kafka на localhost.

```bash
cd satellite && ./gradlew bootRun --args='--spring.profiles.active=local'
cd scheduler && ./gradlew bootRun --args='--spring.profiles.active=local'
cd telemetry-service && ./gradlew bootRun
```

### 8.3. Профили Spring

| Профиль | Назначение |
|---|---|
| `local` | БД и Kafka на localhost |
| `docker` | Адреса во внутренней Docker-сети |
| `test` | Тесты с Testcontainers |

### 8.4. Просмотр сообщений Kafka

```bash
docker exec kafka sh -c "/opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic telemetry --from-beginning"
```

Сообщения передаются в формате Protobuf, поэтому в консоли отображаются как бинарные данные. В логах satellite они выводятся в читаемом виде:

```
Связь-1 получил телеметрию: внутр. 21.51°C / внеш. -79.83°C
```

### 8.5. Порты

| Сервис | REST | gRPC |
|---|---|---|
| satellite | 8080 | - |
| scheduler | 8081 | - |
| telemetry-service | 9092 | 9091 |
| PostgreSQL | 5432 | - |
| Kafka (внутри сети) | - | 9092 |
| Kafka (localhost) | - | 9094 |

## 9. Тесты

```bash
./gradlew test
./gradlew test jacocoTestReport
```

Отчет покрытия: `build/reports/jacoco/test/html/index.html`


## 10. Триаж и приоритизация

### Сводная таблица находок

| ID  | Инструмент | Тип                            | Файл / Компонент                                                        | Severity      |    EPSS | KEV | Решение      | Обоснование                                                                                                                                                                   |
| --- | ---------- | ------------------------------ | ----------------------------------------------------------------------- | ------------- | ------: | --- | ------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 001 | Gitleaks   | Secret scan                    | `gitleaks-report.json`                                                  | —             |       — | Нет | Нет находок  | В отчёте пустой массив `[]`.                                                                                                                                                  |
| 002 | Semgrep    | Static analysis                | `semgrep-report.json`                                                   | —             |       — | Нет | Нет находок  | `results: []`; есть только warnings о partial parsing `satellite\\Dockerfile` и `satellite\\gradlew`.                                                                         |
| 003 | Grype      | CVE-2024-38820                 | `spring-context:6.1.3`                                                  | Medium        | 0.01473 | Нет | Устранить    | Есть фикс `6.1.14`.                                                                                                                                                           |
| 004 | Grype      | CVE-2025-22233                 | `spring-context:6.1.3`                                                  | Low           | 0.00083 | Нет | Устранить    | Есть фикс `6.1.20`.                                                                                                                                                           |
| 005 | Grype      | CVE-2025-41249                 | `spring-core:6.1.3`                                                     | High          | 0.00112 | Нет | Принять риск | Уязвимость актуальна только при `@EnableMethodSecurity` и security-аннотациях в generic superclasses / interfaces; фикс-версии в отчёте нет.                                  |
| 006 | Grype      | CVE-2025-22235                 | `spring-boot:3.2.2`                                                     | High          | 0.00181 | Нет | Принять риск | Риск применим только при использовании `EndpointRequest.to()` для отключённого / неexposed actuator endpoint и при необходимости защищать `/null`; фикс-версии в отчёте нет.  |
| 007 | Grype      | CVE-2024-7254                  | `protobuf-java:3.25.1`                                                  | High          | 0.00134 | Нет | Устранить    | Есть фикс `3.25.5`.                                                                                                                                                           |
| 008 | Grype      | CVE-2024-12798                 | `logback-core:1.4.14`                                                   | Medium        | 0.00169 | Нет | Устранить    | Есть фикс `1.5.13`.                                                                                                                                                           |
| 009 | Grype      | CVE-2024-12801                 | `logback-core:1.4.14`                                                   | Low           | 0.00064 | Нет | Устранить    | Есть фикс `1.5.13`.                                                                                                                                                           |
| 010 | Grype      | CVE-2025-11226                 | `logback-core:1.4.14`                                                   | Medium        | 0.00067 | Нет | Устранить    | Есть фикс `1.5.19`.                                                                                                                                                           |
| 011 | Grype      | CVE-2026-1225                  | `logback-core:1.4.14`                                                   | Low           | 0.00014 | Нет | Устранить    | Есть фикс `1.5.25`.                                                                                                                                                           |
| 012 | ZAP        | Storable and Cacheable Content | `http://localhost:8080/robots.txt`, `http://localhost:8080/sitemap.xml` | Informational |       — | Нет | Принять риск | Это 2 информационные находки по публичным ресурсам; риск низкий, если там нет sensitive / user-specific content.                                                              |

EPSS заполнен только для CVE-находок Grype; для Gitleaks, Semgrep и ZAP — прочерк.

### Варианты решений

| Решение             | Когда применять                   | Что писать в обосновании                                                     |
| ------------------- | --------------------------------- | ---------------------------------------------------------------------------- |
| Устранить           | True positive с реальным риском   | Что именно сделаете и когда                                                  |
| Принять риск        | True positive, но риск приемлем   | Почему риск приемлем, компенсирующие меры, срок пересмотра, владелец решения |
| False positive      | Сканер ошибся                     | Почему это не уязвимость в вашем случае                                      |
| Компенсирующие меры | Патча нет или обновить невозможно | Какие меры снижают риск прямо сейчас                                         |


### Принятие риска

**CVE-2025-41249 (`spring-core:6.1.3`)** — риск принимается, в коде не используется `@EnableMethodSecurity` и нет security-аннотаций на методах generic superclasses / interfaces. Компенсирующие меры: ревью мест использования Spring Security, ограничение scope авторизации, пересмотр перед следующим релизом, владелец решения — команда разработки.

**CVE-2025-22235 (`spring-boot:3.2.2`)** — риск принимается, не используется `EndpointRequest.to()` для отключённого / неexposed actuator endpoint и путь `/null` не требует защиты. Компенсирующие меры: проверить security-конфигурацию, не использовать условие, создающее matcher на `/null`, пересмотр перед следующим релизом, владелец решения — команда разработки.

**ZAP `Storable and Cacheable Content`** — риск принимается для `robots.txt` и `sitemap.xml`, данный сервис возвращает для них 404, но, при дальнейшем добавлении, это публичные статические файлы без чувствительных данных. Компенсирующие меры: при необходимости добавить `Cache-Control: no-cache, no-store, must-revalidate, private`, `Pragma: no-cache`, `Expires: 0`; пересмотр после проверки заголовков, владелец решения — команда разработки.
