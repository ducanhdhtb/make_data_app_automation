# make_date_app_automation

Java + Maven test automation framework using Playwright (UI + API) and JUnit 5.

## Quick Start

Run all tests:

```bash
mvn test
```

Run only API tests:

```bash
mvn test -Papi
```

Run only UI tests:

```bash
mvn test -Pui
```

Run a single test class:

```bash
mvn test -Dtest=LoginTest
```

## Configuration

All config can be set via Java system properties (`-D...`) or environment variables.

- `baseUrl` / `BASE_URL` (default `http://localhost:3002`)
- `apiUrl` / `API_URL` (default `http://localhost:3001/api`)
- `headless` / `HEADLESS` (default `true`)
- `browser` / `BROWSER` (default `chromium`, also supports `firefox`, `webkit`)
- `trace` / `TRACE` (default `false`)
- `artifactsDir` / `ARTIFACTS_DIR` (default `target/test-artifacts`)
- `seedEmail` / `SEED_EMAIL` (default `linh@example.com`)
- `seedPassword` / `SEED_PASSWORD` (default `Password123!`)

Example:

```bash
mvn test -Pui -Dheadless=false -DbaseUrl=http://localhost:3002
```

## Artifacts

On UI test failure, the framework writes artifacts to `target/test-artifacts/...`:

- `screenshot.png`
- `url.txt`
- `trace.zip` (only when `-Dtrace=true`)

## Project Layout

- `src/test/java/com/nearmatch/framework/*`: reusable framework code (config, base fixtures)
- `src/test/java/com/nearmatch/tests/api/*`: API tests
- `src/test/java/com/nearmatch/tests/ui/*`: UI tests

