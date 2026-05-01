# RabbitMQ Web UI Automation Tests

Playwright + JUnit 5 automation tests for the RabbitMQ Management Console.

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker

## Setup

**1. Start RabbitMQ**
```bash
docker compose up -d
```

**2. Install Playwright browser (first time only)**
```bash
mvn exec:java -Dexec.mainClass=com.microsoft.playwright.CLI \
              -Dexec.args="install chromium" \
              -Dexec.classpathScope=test
```

---

## Run all tests

```bash
mvn test
```

> **Headed mode (watch the browser locally)**
> By default the browser runs headless. To see it during a local run, open
> `src/test/java/com/bistechtest/rabbitmq/BaseTest.java` and change:
> ```java
> new BrowserType.LaunchOptions().setHeadless(true).setSlowMo(500)
> ```
> to:
> ```java
> new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(500)
> ```
> Remember to revert this before pushing — the CI workflow requires headless mode.

---

## Run each test separately

**Test 1 — Successful login**
```bash
mvn test -Dtest="LoginTest#testSuccessfulLogin"
```

**Test 2 — Invalid credentials rejected**
```bash
mvn test -Dtest="LoginTest#testInvalidLogin"
```

**Test 3 — Enqueue via API, dequeue via UI**
```bash
mvn test -Dtest="MessageQueueTest#testEnqueueAndDequeue"
```

---

## Screenshots

Tests 1–3 save full-page screenshots to `target/screenshots/`:

| File | Step |
|---|---|
| `01-logged-in.png` | After successful login |
| `02-login-failed.png` | After failed login attempt |
| `02-queues-list.png` | Queues and streams tab |
| `03-queue-detail.png` | Queue detail page |
| `04-message-payload.png` | After retrieving message |

---

## Stop RabbitMQ

```bash
docker compose down
```
