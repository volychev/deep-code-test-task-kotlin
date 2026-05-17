# subscription-service
![Kotlin](https://img.shields.io/badge/Kotlin-ffffff?logo=kotlin&style=for-the-badge&color=ffffff&logoColor=7F52FF) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-ffffff?logo=springboot&style=for-the-badge&color=ffffff&logoColor=6DB33F) ![PostgreSQL](https://img.shields.io/badge/PostgreSQL-ffffff?logo=postgresql&style=for-the-badge&color=ffffff&logoColor=4169E1) ![Flyway](https://img.shields.io/badge/Flyway-ffffff?logo=flyway&style=for-the-badge&color=ffffff&logoColor=CC0000) ![Docker](https://img.shields.io/badge/Docker-ffffff?logo=docker&style=for-the-badge&color=ffffff&logoColor=2496ED) ![Swagger](https://img.shields.io/badge/Swagger-ffffff?logo=swagger&style=for-the-badge&color=ffffff&logoColor=85EA2D)

**subscription-service** — сервис для учёта и управления пользовательскими подписками на различные услуги или тарифы. Реализует полный жизненный цикл подписки, гибкую фильтрацию, автоматическое продление и механизм фонового перевода просроченных подписок в неактивное состояние.

* #### [Техническое задание](task.md)

## Endpoints

> *Полная спецификация Swagger UI доступна локально по адресу:* `http://localhost:{PORT}/swagger-ui/index.html`

Базовый префикс API: `/api` ; Текущая версия: `/v1`

### Пользователи (`Users`)

* `POST`: `/api/v1/users/` — Создание нового пользователя
* `GET`: `/api/v1/users/{user_id}` — Получение пользователя по id
* `PATCH`: `/api/v1/users/{user_id}` — Частичное обновление данных пользователя (с валидацией уникальности)
* `DELETE`: `/api/v1/users/{user_id}` — Удаление пользователя
* `GET`: `/api/v1/users/` — Получение списка пользователей с пагинацией

### Сервисы (`Services`)

* `POST`: `/api/v1/services` — Добавление нового сервиса
* `GET`: `/api/v1/services/{id}` — Получение сервиса по id
* `PATCH`: `/api/v1/services/{id}` — Частичное обновление данных о сервисе
* `DELETE`: `/api/v1/services/{id}` — Удаление сервиса
* `GET`: `/api/v1/services` — Получение списка сервисов с пагинацией

### Подписки (`Subscriptions`)

* `POST`: `/api/v1/subscriptions` — Создание новой подписки (с валидацией дат и цены)
* `GET`: `/api/v1/subscriptions` — Список подписок с фильтрами
* `GET`: `/api/v1/subscriptions/{id}` — Получение подписки по id
* `PATCH`: `/api/v1/subscriptions/{id}/activate` — Активация подписки (запрещена для просроченных)
* `PATCH`: `/api/v1/subscriptions/{id}/suspend` — Приостановка подписки
* `PATCH`: `/api/v1/subscriptions/{id}/cancel` — Отмена подписки
* `PATCH`: `/api/v1/subscriptions/{id}/renew` — Продление подписки с обновлением даты окончания
* `GET`: `/api/v1/subscriptions/user/{userId}/active` — Список всех активных подписок конкретного пользователя

## Требования

* JDK 21+
* Gradle 8.x
* Docker & Docker Compose
* PostgreSQL (для контейнеров) или H2 (для локального дебага)

## Развертывание и использование

1. Склонируйте репозиторий и создайте файл `.env` на основе шаблона *[(.env.example)](.env.example)*:

```env
# API
HOST=0.0.0.0
PORT=8080

# PostgreSQL
POSTGRES_DB=subscription_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres

# Spring DataSource
SPRING_DATASOURCE_URL=jdbc:postgresql://subscription-postgres:5432/subscription_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
```

2. Запустите проект при помощи `docker-compose`:

```bash
docker compose up -d --build
```
