package com.nearmatch.framework.config;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class TestConfig {
  private TestConfig() {}

  private static volatile Properties FILE_PROPS;

  public static String baseUrl() {
    return firstNonBlank(
      System.getProperty("baseUrl"),
      System.getenv("BASE_URL"),
      fileProp("baseUrl"),
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
      fileProp("apiUrl"),
      "http://localhost:3001/api"
    );
    return raw.endsWith("/") ? raw : raw + "/";
  }

  public static boolean headless() {
    return parseBool(firstNonBlank(
      System.getProperty("headless"),
      System.getenv("HEADLESS"),
      fileProp("headless"),
      "true"
    ), true);
  }

  public static String browserName() {
    return firstNonBlank(
      System.getProperty("browser"),
      System.getenv("BROWSER"),
      fileProp("browser"),
      "chromium"
    );
  }

  public static double slowMoMs() {
    return parseDouble(firstNonBlank(
      System.getProperty("slowMoMs"),
      System.getenv("SLOW_MO_MS"),
      fileProp("slowMoMs"),
      "0"
    ), 0.0);
  }

  public static boolean trace() {
    return parseBool(firstNonBlank(
      System.getProperty("trace"),
      System.getenv("TRACE"),
      fileProp("trace"),
      "false"
    ), false);
  }

  public static Path artifactsDir() {
    return Path.of(firstNonBlank(
      System.getProperty("artifactsDir"),
      System.getenv("ARTIFACTS_DIR"),
      fileProp("artifactsDir"),
      "target/test-artifacts"
    ));
  }

  public static String seedEmail() {
    return firstNonBlank(
      System.getProperty("seedEmail"),
      System.getenv("SEED_EMAIL"),
      fileProp("seedEmail"),
      "linh@example.com"
    );
  }

  public static String seedPassword() {
    return firstNonBlank(
      System.getProperty("seedPassword"),
      System.getenv("SEED_PASSWORD"),
      fileProp("seedPassword"),
      "Password123!"
    );
  }

  private static String fileProp(String key) {
    Properties p = fileProps();
    String v = p.getProperty(key);
    return v == null ? null : v.trim();
  }

  private static Properties fileProps() {
    Properties current = FILE_PROPS;
    if (current != null) return current;
    synchronized (TestConfig.class) {
      if (FILE_PROPS != null) return FILE_PROPS;

      Properties p = new Properties();
      // Optional override: load from filesystem
      String configFile = firstNonBlank(System.getProperty("configFile"), System.getenv("CONFIG_FILE"));
      if (configFile != null) {
        try (InputStream in = Files.newInputStream(Path.of(configFile))) {
          p.load(in);
        } catch (Exception ignored) {
          // best-effort: fall back to classpath defaults
        }
      } else {
        // Default: load from test classpath
        try (InputStream in = TestConfig.class.getClassLoader().getResourceAsStream("test.properties")) {
          if (in != null) p.load(in);
        } catch (Exception ignored) {
          // best-effort
        }
      }

      FILE_PROPS = p;
      return p;
    }
  }

  private static boolean parseBool(String raw, boolean defaultValue) {
    if (raw == null) return defaultValue;
    String v = raw.trim().toLowerCase();
    if (v.isEmpty()) return defaultValue;
    return v.equals("true") || v.equals("1") || v.equals("yes") || v.equals("y") || v.equals("on");
  }

  private static double parseDouble(String raw, double defaultValue) {
    if (raw == null) return defaultValue;
    try {
      return Double.parseDouble(raw.trim());
    } catch (Exception ignored) {
      return defaultValue;
    }
  }

  private static String firstNonBlank(String... values) {
    for (String value : values) {
      if (value != null && !value.trim().isEmpty()) return value.trim();
    }
    return null;
  }
}
