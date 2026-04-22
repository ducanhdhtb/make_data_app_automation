package com.nearmatch.framework.config;

import java.nio.file.Path;

public final class TestConfig {
  private TestConfig() {}

  public static String baseUrl() {
    return firstNonBlank(
      System.getProperty("baseUrl"),
      System.getenv("BASE_URL"),
      "http://localhost:3002"
    );
  }

  /**
   * Default: http://localhost:3001/api
   *
   * Playwright's APIRequestContext resolves relative paths like a browser.
   * For an "/api" base path, we need a trailing slash + relative (no leading "/") paths.
   */
  public static String apiUrl() {
    String raw = firstNonBlank(
      System.getProperty("apiUrl"),
      System.getenv("API_URL"),
      "http://localhost:3001/api"
    );
    return raw.endsWith("/") ? raw : raw + "/";
  }

  public static boolean headless() {
    return Boolean.parseBoolean(firstNonBlank(
      System.getProperty("headless"),
      System.getenv("HEADLESS"),
      "true"
    ));
  }

  public static String browserName() {
    return firstNonBlank(
      System.getProperty("browser"),
      System.getenv("BROWSER"),
      "chromium"
    );
  }

  public static boolean trace() {
    return Boolean.parseBoolean(firstNonBlank(
      System.getProperty("trace"),
      System.getenv("TRACE"),
      "false"
    ));
  }

  public static Path artifactsDir() {
    return Path.of(firstNonBlank(
      System.getProperty("artifactsDir"),
      System.getenv("ARTIFACTS_DIR"),
      "target/test-artifacts"
    ));
  }

  public static String seedEmail() {
    return firstNonBlank(
      System.getProperty("seedEmail"),
      System.getenv("SEED_EMAIL"),
      "linh@example.com"
    );
  }

  public static String seedPassword() {
    return firstNonBlank(
      System.getProperty("seedPassword"),
      System.getenv("SEED_PASSWORD"),
      "Password123!"
    );
  }

  private static String firstNonBlank(String... values) {
    for (String value : values) {
      if (value != null && !value.trim().isEmpty()) return value.trim();
    }
    return null;
  }
}

