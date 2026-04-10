# Система управления спутниковой группировкой

## 1. Общее описание

Проект представляет собой backend-сервис для управления группировками искусственных спутников Земли. Система позволяет создавать группировки, добавлять в них спутники связи (Communication) и наблюдения (Imaging), активировать спутники и выполнять их целевые миссии (передача данных или съемка). Реализация выполнена на Java 21 с использованием Spring Boot и следует принципам SOLID и паттернам проектирования GoF.

## 2. Технологии

- Java 21
- Spring Boot 3.2 (Spring MVC, Spring Data JPA, Spring Validation, Spring AOP)
- PostgreSQL (через Docker Compose)
- Liquibase для управления схемой БД
- Lombok
- MapStruct для маппинга DTO
- SpringDoc OpenAPI (Swagger) для документирования API
- Docker / Docker Compose для контейнеризации
- JUnit 5, MockMvc, Testcontainers для тестирования

## 3. Архитектура

Проект разделен на логические модули:

- `common` – общие утилиты, обработка исключений, базовые интерфейсы CRUD, AOP-аспекты.
- `constellation` – модуль группировок:
    - сущность `Constellation`
    - репозиторий `ConstellationRepository` (JPA)
    - сервисы: `ConstellationEntityService` (CRUD для сущности) и `ConstellationService` (бизнес-логика)
    - контроллер `ConstellationController` с REST API
- `satellite` – модуль спутников:
    - иерархия сущностей: абстрактный `Satellite`, конкретные `CommunicationSatellite` и `ImagingSatellite`
    - репозиторий `SatelliteRepository`
    - сервисы: `SatelliteEntityService`, `SatelliteService`
    - контроллер `SatelliteController`
- `operations` – модуль высокоуровневых операций:
    - `SpaceOperationCenterService` – фасад над всеми бизнес-сценариями
    - `SpaceOperationCenterController` – REST API для фасада

## 4. Паттерны проектирования

### 4.1. Порождающие паттерны (Creational)

**Factory Method** — создание спутников через `SatelliteFactory`. Каждый тип спутника имеет собственную фабрику (`CommunicationSatelliteFactory`, `ImagingSatelliteFactory`). `SatelliteCreationService` выбирает нужную фабрику по типу спутника.

```
SatelliteCreationService
  ├── CommunicationSatelliteFactory  →  CommunicationSatellite
  └── ImagingSatelliteFactory        →  ImagingSatellite
```

**Builder** — построение сущностей `Constellation`, `EnergySystem` и DTO через builder-паттерн.

### 4.2. Структурные паттерны (Structural)

**Facade** — `SpaceOperationCenterService` скрывает сложность взаимодействия между `ConstellationService` и `SatelliteService`, предоставляя клиенту простой высокоуровневый API:

| Метод | Что скрывает |
|---|---|
| `addSatellites(request)` | findOrCreate группировки + цикл добавления спутников |
| `executeMission(request)` | активация + фильтрация по типу + запуск миссий + агрегация |
| `activateAndExecuteAll(name)` | активация всех спутников и запуск всех миссий |
| `emergencyShutdown(name)` | деактивация всех спутников группировки |
| `getSystemStatus()` | агрегированный статус по всем группировкам |
| `getHealthReport()` | сводка критических и неактивных спутников |

**Decorator (через AOP)** — аннотация `@MeasureExecutionTime` замеряет время выполнения метода без изменения исходного кода. Spring AOP создает прокси-объект, оборачивающий аннотированный метод:

```
Клиент → Proxy (ExecutionTimeAspect) → реальный метод
```

Пример использования:
```java
@MeasureExecutionTime(operationName = "Активация всех спутников группировки")
public List<SatelliteResponse> activateAllSatellites(Long constellationId) { ... }
```

Пример вывода в консоль:
```
[TIMING] ⏱ Активация всех спутников группировки | Время выполнения: 38 мс
```

### 4.3. Поведенческие паттерны (Behavioral)

**Strategy** — `SatelliteUpdater` определяет стратегию обновления спутника в зависимости от его типа. `CommunicationSatelliteUpdater` и `ImagingSatelliteUpdater` реализуют каждый свою стратегию.

**Template Method** — абстрактный `Satellite` определяет шаблон поведения (активация, расход батареи, состояние), а конкретные классы реализуют метод `performMission()`.

## 5. Принцип инверсии зависимостей (DIP)

Сервисы зависят от абстракций, а не от конкретных реализаций:

- `ConstellationService` зависит от `ConstellationEntityService` (интерфейс), а не от `ConstellationEntityServiceImpl`
- `SatelliteServiceImpl` получает список всех `SatelliteFactory` через конструктор — добавление нового типа спутника не требует изменения кода сервиса
- Репозитории спроектированы как интерфейсы, реализации предоставляет Spring Data JPA
- Внедрение зависимостей осуществляется через конструктор (`@RequiredArgsConstructor`)

## 6. Соответствие техническому заданию

Требуемые методы и их аналоги в коде:

| Требование ТЗ | Реализация |
|---|---|
| `createAndSaveConstellation(String name)` | `create(ConstellationRequest)` в `ConstellationService` |
| `addSatelliteToConstellation(name, satellite)` | `addSatellite(Long id, SatelliteRequest)` |
| `executeConstellationMission(name)` | `executeAllMissions(Long id)` |
| `activateAllSatellites(name)` | `activateAllSatellites(Long id)` |
| `showConstellationStatus(name)` | `getConstellationStatus(Long id)` и `getConstellationStatus(String name)` |

Все операции также доступны через фасад `SpaceOperationCenterService`.

### 6.1 Микросервисы

В проекте реализовано 2 сервиса, один из них выступает в роли бекенд части для обработки поступающих данных. Второй сервис-планировщик отправляет заранее заданные в конфигурации данные с определенной периодичностью.

Сервисы коммуницируют между собой средствами REST API при помощи Feign библиотеки входящей в Spring Cloud.

Гарантия отправки и доставки сообщений из планировщика осуществляется средствами паттерна Outboxing Transactional

## 7. Инициализация данных

В классе `DataInitializer` (профиль `!test`) создаются тестовые группировки и спутники при старте приложения, если база пуста.

## 8. Сборка и запуск

### 8.1. Через Docker Compose (рекомендуется)

```bash
docker-compose up -d
```

Поднимает три контейнера: PostgreSQL, pgAdmin (порт 5050, admin@bauman.ru / admin), приложение (порт 8080).

Swagger UI: `http://localhost:8080/swagger-ui.html`

```bash
docker-compose down       # остановить
docker-compose build app  # пересобрать образ
```

### 8.2. Через Gradle (без Docker)

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

Требует запущенного PostgreSQL и настроенного `application-local.yml`.

### 8.3. Профили Spring

| Профиль | Назначение |
|---|---|
| `local` | Локальный запуск без Docker |
| `docker` | Запуск внутри Docker-контейнера |
| `test` | Автоматически активируется при тестах, использует Testcontainers |

### 8.4. Переменные окружения

| Переменная | Описание |
|---|---|
| `SPRING_DATASOURCE_URL` | JDBC URL базы данных |
| `SPRING_DATASOURCE_USERNAME` | Имя пользователя |
| `SPRING_DATASOURCE_PASSWORD` | Пароль |
| `SPRING_PROFILES_ACTIVE` | Активный профиль |

## 9. Тесты

```bash
./gradlew test
```

```bash
./gradlew test jacocoTestReport
```

Отчет: `build/reports/tests/test/index.html`

Отчет покрытия (JaCoCo): `build/reports/jacoco/test/html/index.html`

В проекте реализованы:
- Модульные тесты (Mockito) — `ConstellationServiceImplTest`, `SatelliteServiceImplTest`, `SatelliteFactoryTest`
- Интеграционные тесты (Testcontainers + PostgreSQL) — `ConstellationIntegrationTest`, `SatelliteIntegrationTest`, `ConstellationRepositoryIntegrationTest`
- MVC-тесты (MockMvc) — `ConstellationControllerTest`, `SatelliteControllerTest`