package com.nearmatch.framework.tools;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;

/**
 * Installs Playwright browsers without invoking Playwright CLI (which may System.exit()).
 *
 * It triggers installation by launching and closing the configured browser.
 */
public final class PlaywrightInstaller {
  private PlaywrightInstaller() {}

  public static void main(String[] args) {
    String browserName = get("browser", "BROWSER", "chromium").toLowerCase();

    try (Playwright playwright = Playwright.create()) {
      BrowserType browserType = switch (browserName) {
        case "chromium" -> playwright.chromium();
        case "firefox" -> playwright.firefox();
        case "webkit" -> playwright.webkit();
        default -> throw new IllegalArgumentException("Unsupported browser: " + browserName);
      };

      // Launch once to force-download the browser if missing, then close.
      try (Browser browser = browserType.launch(new BrowserType.LaunchOptions().setHeadless(true))) {
        // no-op
      }
    }
  }

  private static String get(String sysProp, String envVar, String defaultValue) {
    String v = System.getProperty(sysProp);
    if (v != null && !v.trim().isEmpty()) return v.trim();
    v = System.getenv(envVar);
    if (v != null && !v.trim().isEmpty()) return v.trim();
    return defaultValue;
  }
}

