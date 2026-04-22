# make_date_app_automation

Java + Maven test automation framework using Playwright (UI + API), TestNG, and Allure reporting.

## Quick Start

Run all tests:

```bash
./mvnw test
```

Run only API tests:

```bash
./mvnw test -Papi
```

Run only UI tests:

```bash
./mvnw test -Pui
```

Run a single test class:

```bash
./mvnw test -Dtest=LoginTest
```

## Configuration

All config can be set via Java system properties (`-D...`) or environment variables.
Defaults live in `src/test/resources/test.properties` (or point to a custom file via `-DconfigFile=...` / `CONFIG_FILE=...`).

- `baseUrl` / `BASE_URL` (default `http://localhost:3002`)
- `apiUrl` / `API_URL` (default `http://localhost:3001/api`)
- `headless` / `HEADLESS` (default `true`)
- `browser` / `BROWSER` (default `chromium`, also supports `firefox`, `webkit`)
- `slowMoMs` / `SLOW_MO_MS` (default `0`)
- `trace` / `TRACE` (default `false`)
- `artifactsDir` / `ARTIFACTS_DIR` (default `target/test-artifacts`)
- `seedEmail` / `SEED_EMAIL` (default `linh@example.com`)
- `seedPassword` / `SEED_PASSWORD` (default `Password123!`)

Example:

```bash
./mvnw test -Pui -Dheadless=false -DbaseUrl=http://localhost:3002
```

## Artifacts

On UI test failure, the framework writes artifacts to `target/test-artifacts/...`:

- `screenshot.png`
- `url.txt`
- `trace.zip` (only when `-Dtrace=true`)

## Allure Report

Test runs write Allure results to `target/allure-results`.

Generate a static report:

```bash
./mvnw allure:report
```

Or open an interactive report (requires Allure CLI installed on your machine):

```bash
allure serve target/allure-results
```

## Project Layout

- `src/test/java/com/nearmatch/framework/*`: reusable framework code (config, base fixtures)
- `src/test/java/com/nearmatch/tests/api/*`: API tests
- `src/test/java/com/nearmatch/tests/ui/*`: UI tests
