# Система управления спутниковой группировкой

## 1. Общее описание

Проект представляет собой backend-сервис для управления группировками искусственных спутников Земли. Система позволяет создавать группировки, добавлять в них спутники связи (Communication) и наблюдения (Imaging), активировать спутники и выполнять их целевые миссии (передача данных или съемка). Реализация выполнена на Java 21 с использованием Spring Boot и следует принципам SOLID, с особым вниманием к принципу инверсии зависимостей (Dependency Inversion).

## 2. Технологии

- Java 21
- Spring Boot 3.2 (Spring MVC, Spring Data JPA, Spring Validation)
- PostgreSQL (через Docker Compose)
- Liquibase для управления схемой БД
- Lombok
- MapStruct для маппинга DTO
- SpringDoc OpenAPI (Swagger) для документирования API
- Docker / Docker Compose для контейнеризации
- JUnit 5, MockMvc, Testcontainers для тестирования

## 3. Архитектура

Проект разделен на логические модули:

- `common` – общие утилиты, обработка исключений, базовые интерфейсы CRUD.
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

Взаимодействие между модулями осуществляется через интерфейсы сервисов, что обеспечивает слабую связанность.

## 4. Соответствие техническому заданию

Техническое задание (ТЗ) предполагало следующую структуру:

- Хранилище группировок `ConstellationRepository` на основе `Map<String, SatelliteConstellation>`.
- Единый сервис `SpaceOperationCenterService` с методами:
    - `createAndSaveConstellation(String name)`
    - `addSatelliteToConstellation(String constellationName, Satellite satellite)`
    - `executeConstellationMission(String constellationName)`
    - `activateAllSatellites(String constellationName)`
    - `showConstellationStatus(String constellationName)`
- Использование Spring Boot только для DI, остальное – простые Java-классы.
- Демонстрация работы через `main` с выводом в консоль.

Предоставленная реализация значительно расширяет требования, оставаясь в рамках поставленной задачи – продемонстрировать принцип инверсии зависимостей. Ниже подробно описаны отличия.

### 4.1. Хранилище данных

**ТЗ:** `ConstellationRepository` – класс с коллекцией `Map<String, SatelliteConstellation>`.

**Реализация:** Вместо самодельного репозитория используется Spring Data JPA и реляционная БД PostgreSQL. Интерфейс `ConstellationRepository` расширяет `JpaRepository`, предоставляя все необходимые CRUD-методы, а также дополнительные запросы (`findByName`, `findAllWithSatellites`). Это позволяет:

- Реально использовать внедрение зависимостей (Spring предоставляет реализацию репозитория).
- Обеспечить транзакционность и целостность данных.
- Легко перейти на другую БД, изменив только конфигурацию (инверсия зависимостей соблюдена).

### 4.2. Сервисный слой

**ТЗ:** Один класс `SpaceOperationCenterService`, который содержит все методы.

**Реализация:** Создано несколько сервисов с четкими зонами ответственности:

- `ConstellationEntityService` – операции с сущностью группировки (CRUD). Название отражает работу с JPA-сущностью.
- `ConstellationService` – бизнес-логика, связанная с группировками. Именно он реализует методы, аналогичные требуемым в ТЗ, но с немного измененными сигнатурами.
- `SatelliteEntityService` и `SatelliteService` – аналогично для спутников.

Такое разделение соответствует принципу единственной ответственности (Single Responsibility) и упрощает тестирование. Кроме того, это естественный способ организовать код в Spring-приложении, где каждый бин отвечает за свою область.

### 4.3. Наименование методов

Требуемые методы и их аналоги в коде:

| Требование ТЗ | Реализация | Пояснение |
|---------------|------------|-----------|
| `createAndSaveConstellation(String name)` | `create(ConstellationRequest request)` в `ConstellationService` | Вместо отдельного метода с одним параметром-именем, используется DTO, что позволяет передавать дополнительные поля (описание). Это расширяет функциональность без потери основной идеи. |
| `addSatelliteToConstellation(String constellationName, Satellite satellite)` | `addSatellite(Long constellationId, SatelliteRequest request)` | Вместо имени используется идентификатор группировки, что надежнее (имена могут меняться). Вместо готового объекта спутника передается запрос DTO, что позволяет создавать спутник внутри метода, инкапсулируя логику создания. |
| `executeConstellationMission(String constellationName)` | `executeAllMissions(Long constellationId)` | Аналогично, используется ID, а не имя. Название уточняет, что выполняются миссии всех спутников. |
| `activateAllSatellites(String constellationName)` | `activateAllSatellites(Long constellationId)` – метод присутствует, сигнатура изменена на ID. | – |
| `showConstellationStatus(String constellationName)` | `getConstellationStatus(Long id)` и `getConstellationStatus(String constellationName)` (перегрузка) | Предоставлены оба варианта для гибкости. |

Таким образом, все ключевые операции реализованы, хотя и с несколько иной сигнатурой, что обусловлено использованием REST API и работой с БД.

### 4.4. Дополнительные методы и классы

В проекте появились:

- CRUD-методы для группировок и спутников (`findAll`, `update`, `delete`).
- Специализированные классы-создатели (`SatelliteCreator`) и обновляторы (`SatelliteUpdater`) для разных типов спутников, что реализует принцип открытости/закрытости (OCP).
- Глобальный обработчик исключений.
- Полноценное REST API с валидацией и документацией Swagger.
- Интеграционные тесты с Testcontainers.

Эти дополнения не противоречат ТЗ, а лишь демонстрируют более глубокое понимание разработки и готовность к реальной эксплуатации.

### 4.5. Использование Spring Boot

**ТЗ:** Добавить аннотации `@SpringBootApplication`, `@Service`/`@Component`, использовать `ConfigurableApplicationContext` для получения бинов.

**Реализация:** Все требования выполнены:

- `Seminar3Application` помечен `@SpringBootApplication`.
- Все сервисы имеют аннотации `@Service`.
- В `main` получается контекст и извлекаются бины (`ConstellationService` и др.) для демонстрации работы (в коде это закомментировано, но ранее использовалось). В текущей версии основной класс запускает приложение, а демонстрация происходит через вызовы API или через `DataInitializer`.

## 5. Принцип инверсии зависимостей (DIP) в реализации

Инверсия зависимостей требует, чтобы модули верхнего уровня не зависели от модулей нижнего уровня, а оба зависели от абстракций. В проекте это соблюдается:

- Сервисы (`ConstellationService`, `SatelliteService`) зависят от абстракций (`ConstellationEntityService`, `SatelliteEntityService`, `SatelliteCreator`), а не от конкретных реализаций.
- Репозитории спроектированы как интерфейсы, их реализации предоставляет Spring Data.
- Создатели и обновляторы спутников регистрируются как бины и внедряются через интерфейс `SatelliteCreator`, что позволяет легко добавлять новые типы.
- Внедрение зависимостей осуществляется через конструктор (с использованием `@RequiredArgsConstructor`), что считается лучшей практикой и упрощает тестирование.

Пример: `SatelliteServiceImpl` получает список всех `SatelliteCreator` через конструктор, а затем строит карту для быстрого доступа. Если появится новый тип спутника, достаточно добавить еще один бин, реализующий `SatelliteCreator`, – код сервиса не меняется.

## 6. Инициализация данных

В классе `DataInitializer` (помечен `@Component` и активируется профилем `!test`) создаются тестовые группировки и спутники при старте приложения, если база пуста. Это удобно для демонстрации и отладки. Методы инициализации используют те же сервисы, что и основное приложение, что подтверждает правильность DI.

## 7. Сборка и запуск

Проект использует Gradle в качестве системы сборки. Для удобства предоставлен Gradle Wrapper (`gradlew`), что позволяет запускать сборку без предустановленного Gradle.

### 7.1. Запуск приложения

#### Через Gradle (без Docker)

Перед запуском убедитесь, что PostgreSQL доступен локально или удаленно, и настройте параметры подключения. Конфигурация для локального запуска обычно задается в профиле `local`.

**Шаги:**

1. Создайте базу данных (например, `satellite_db`).
2. Настройте файл `application-local.yml` или используйте переменные окружения:
    - `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/satellite_db`
    - `SPRING_DATASOURCE_USERNAME=postgres`
    - `SPRING_DATASOURCE_PASSWORD=postgres`
3. Выполните команду:

   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

   Приложение запустится на порту 8080 (по умолчанию). Swagger UI будет доступен по адресу `http://localhost:8080/swagger-ui.html`.

#### Через Docker Compose (рекомендуемый способ)

В проекте подготовлен файл `docker-compose.yml`, который поднимает PostgreSQL, pgAdmin и само приложение в контейнерах.

**Шаги:**

1. Убедитесь, что Docker и Docker Compose установлены.
2. В корневой директории проекта выполните:

   ```bash
   docker-compose up -d
   ```

   Будут созданы и запущены три контейнера:
    - `seminar-postgres` – база данных PostgreSQL.
    - `seminar-pgadmin` – веб-интерфейс для управления БД (доступен на порту 5050, логин/пароль по умолчанию: admin@bauman.ru / admin).
    - `seminar-app` – само Spring Boot приложение.

3. После успешного запуска приложение будет доступно на порту 8080. Swagger UI: `http://localhost:8080/swagger-ui.html`.

Для остановки контейнеров используйте:

```bash
docker-compose down
```

При необходимости можно пересобрать образ приложения:

```bash
docker-compose build app
```

### 7.2. Запуск тестов

В проекте реализованы модульные тесты (с Mockito) и интеграционные тесты, использующие Testcontainers для автоматического поднятия PostgreSQL в контейнере во время тестирования.

Для запуска всех тестов выполните команду:

```bash
./gradlew test
```

Тесты будут выполнены в изолированном окружении. Testcontainers автоматически скачает Docker-образ PostgreSQL (если его нет локально) и запустит контейнер на время тестов. По окончании тестов контейнер будет остановлен и удален.

Для просмотра отчетов о тестировании откройте файл `build/reports/tests/test/index.html` в браузере.

### 7.3. Профили Spring

В приложении используются следующие профили:

- `local` – для локального запуска без Docker. Требует наличия запущенного PostgreSQL и соответствующих настроек в `application-local.yml`.
- `docker` – профиль, используемый внутри Docker-контейнера (параметры БД берутся из переменных окружения, заданных в `docker-compose.yml`).
- `test` – активируется автоматически при запуске тестов. Отключает Liquibase (или устанавливает `ddl-auto=create-drop`), чтобы тесты работали с чистой схемой.

### 7.4. Переменные окружения

Для конфигурации подключения к базе данных можно использовать переменные окружения:

- `SPRING_DATASOURCE_URL` – JDBC URL базы данных.
- `SPRING_DATASOURCE_USERNAME` – имя пользователя.
- `SPRING_DATASOURCE_PASSWORD` – пароль.
- `SPRING_PROFILES_ACTIVE` – активный профиль.

При запуске через Docker Compose эти переменные уже заданы в файле `docker-compose.yml`. При локальном запуске их можно установить в командной строке перед вызовом `bootRun`, например:

```bash
./gradlew bootRun
```

### 7.5. Дополнительные команды Gradle

- **Сборка проекта без запуска тестов:**

  ```bash
  ./gradlew build -x test
  ```

- **Очистка сборочной директории:**

  ```bash
  ./gradlew clean
  ```
  
# 8. Тесты

```shell
 ./gradlew test
```

Генерация отчета JaCoCo

```shell
./gradlew test jacocoTestReport
```
