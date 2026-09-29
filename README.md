# Currency Exchange API

> REST API для работы с валютами и обменными курсами. Позволяет просматривать, создавать и редактировать валюты и курсы, а также конвертировать произвольные суммы из одной валюты в другую.
>
> REST API for currencies and exchange rates. Supports viewing, creating, and editing currencies and rates, as well as converting arbitrary amounts between currencies.

[![Java](https://img.shields.io/badge/Java-17-orange)](https://openjdk.org/projects/jdk/17/)
[![Servlet API](https://img.shields.io/badge/Servlet_API-6.0-blue)](https://jakarta.ee/specifications/servlet/6.0/)
[![SQLite](https://img.shields.io/badge/SQLite-3-003B57)](https://www.sqlite.org/)
[![Tomcat](https://img.shields.io/badge/Tomcat-10.1-yellow)](https://tomcat.apache.org/)
[![Tests](https://img.shields.io/badge/Tests-98%2F98-success)](#-тестирование--testing)
[![License](https://img.shields.io/badge/License-Educational-lightgrey)](#-лицензия--license)

---

## 🌐 Live Demo

**🔗 [http://77.221.142.17/](http://77.221.142.17/)**

Развёрнуто на VPS (Aeza, Ubuntu 26.04) под управлением Tomcat 10 + Nginx (reverse-proxy).

Deployed on VPS (Aeza, Ubuntu 26.04) with Tomcat 10 + Nginx (reverse-proxy).

---

## 🛠 Стек технологий / Tech Stack

| Компонент | Технология |
|-----------|------------|
| **Язык / Language** | Java 17 |
| **Web / HTTP** | Jakarta Servlet API 6.0 (без фреймворков) |
| **База данных / Database** | SQLite + JDBC |
| **JSON** | Jackson Databind |
| **Логирование / Logging** | SLF4J + Logback |
| **Сборка / Build** | Maven (WAR) |
| **Сервер / Server** | Apache Tomcat 10.1 |
| **Reverse Proxy** | Nginx |

---

## ✨ Функционал / Features

**Валюты / Currencies**
- Просмотр списка валют и отдельных валют по коду · List all currencies and get a single currency by code
- Добавление новых валют · Add new currencies

**Курсы / Exchange Rates**
- Просмотр списка обменных курсов и курсов по валютной паре · List all exchange rates and get a rate by currency pair
- Создание и обновление обменных курсов · Create and update exchange rates

**Конвертация / Conversion**
- Перевод суммы из одной валюты в другую по трём сценариям · Convert amounts between currencies using three scenarios:
    - прямой курс · direct rate
    - обратный курс · reverse rate
    - кросс-курс через USD · cross-rate via USD

---

## 📡 API Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| `GET` | `/currencies` | Список всех валют / All currencies |
| `GET` | `/currency/{code}` | Одна валюта / Single currency |
| `POST` | `/currencies` | Создать валюту / Create currency |
| `GET` | `/exchangeRates` | Список всех курсов / All exchange rates |
| `GET` | `/exchangeRate/{pair}` | Один курс / Single rate (e.g. `USDEUR`) |
| `POST` | `/exchangeRates` | Создать курс / Create rate |
| `PATCH` | `/exchangeRate/{pair}` | Обновить курс / Update rate |
| `GET` | `/exchange?from=X&to=Y&amount=Z` | Конвертация / Convert |
| `GET` | `/health` | Проверка доступности / Health check |

**Формат ошибок / Error format:**
```json
{ "message": "Валюта не найдена" }
```

**HTTP-коды:** `200`, `201`, `400`, `404`, `409`, `500`

### Пример / Example

```bash
# Список валют
curl http://77.221.142.17/currencies

# Конвертация
curl "http://77.221.142.17/exchange?from=USD&to=RUB&amount=100"
```

```json
{
    "baseCurrency": { "id": 1, "name": "United States dollar", "code": "USD", "sign": "$" },
    "targetCurrency": { "id": 3, "name": "Russian Ruble", "code": "RUB", "sign": "₽" },
    "rate": 90.5,
    "amount": 100,
    "convertedAmount": 9050.00
}
```

---

## ✅ Тестирование / Testing

API прошёл **автоматическое тестирование** ботом — **98/98 кейсов пройдено**:

- ✅ Все CRUD-операции для валют и курсов
- ✅ Все три сценария конвертации (прямой, обратный, кросс-курс через USD)
- ✅ Валидация входных данных (пустые поля, некорректные значения, длина `sign`)
- ✅ Обработка ошибок (400, 404, 409)
- ✅ Корректная структура JSON-ответов
- ✅ Правильные HTTP-коды и `Content-Type`

---

## 📂 Структура проекта / Project Structure

```
src/main/java/com/boo4er/currencyexchange/
├── config/       — слушатели / listeners
├── dao/          — доступ к БД / data access
├── dto/          — Data Transfer Objects
├── exception/    — иерархия исключений / exceptions
├── filter/       — сервлет-фильтры / filters
├── model/        — модели данных / domain models
├── servlet/      — REST-контроллеры / servlets
└── util/         — утилиты / utilities
```

---

## 🚀 Запуск локально / How to Run Locally

### Требования / Requirements

- Java 17+
- Maven 3.8+
- Apache Tomcat 10

### Шаги / Steps

**1. Клонировать / Clone**
```bash
git clone https://github.com/ptkvlsk/currency-exchange.git
cd currency-exchange
```

**2. Собрать WAR / Build WAR**
```bash
mvn clean package
```

**3. Развернуть в Tomcat / Deploy to Tomcat**

Скопируйте `target/currency-exchange.war` в `<tomcat>/webapps/` и запустите Tomcat.
Copy `target/currency-exchange.war` to `<tomcat>/webapps/` and start Tomcat.

**4. Открыть / Open**
```
http://localhost:8080/currency-exchange/
```

База данных SQLite создастся автоматически при первом запуске.
The SQLite database is created automatically on first launch.

---

## 📸 Скриншоты / Screenshots

![Frontend](docs/screenshots/frontend.png)

> **Note:** Для визуального тестирования API подключён тестовый фронтенд от [Сергея Жукова](https://github.com/zhukovsd/currency-exchange-frontend). Backend полностью написан мной.
>
> The test frontend from [Sergey Zhukov](https://github.com/zhukovsd/currency-exchange-frontend) is used for API visual testing. The backend is fully my own work.

---

## 👤 Автор / Author

**ptkvlsk** — [@ptkvlsk](https://github.com/ptkvlsk)

Учебный проект в рамках курса [«Java Backend Learning Course»](https://zhukovsd.github.io/java-backend-learning-course/) Сергея Жукова.
Educational project based on [Sergey Zhukov's Java Backend Learning Course](https://zhukovsd.github.io/java-backend-learning-course/).

---

## 📄 Лицензия / License

Учебный проект. Свободен для использования в образовательных целях.
Educational project. Free to use for educational purposes.