package com.nearmatch.framework.ui;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.options.AriaRole;
import com.nearmatch.framework.config.TestConfig;
import com.nearmatch.framework.junit.TestOutcome;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class BaseUiTest {
  protected static String baseUrl;
  private static Playwright playwright;
  protected static Browser browser;

  protected BrowserContext context;
  protected Page page;

  private boolean tracingStarted;

  @RegisterExtension
  final TestOutcome outcome = new TestOutcome();

  @BeforeAll
  static void beforeAll() {
    baseUrl = TestConfig.baseUrl();
    boolean headless = TestConfig.headless();

    playwright = Playwright.create();

    BrowserType browserType = switch (TestConfig.browserName().toLowerCase()) {
      case "chromium" -> playwright.chromium();
      case "firefox" -> playwright.firefox();
      case "webkit" -> playwright.webkit();
      default -> throw new IllegalArgumentException("Unsupported browser: " + TestConfig.browserName());
    };

    browser = browserType.launch(new BrowserType.LaunchOptions().setHeadless(headless));
  }

  @AfterAll
  static void afterAll() {
    if (browser != null) browser.close();
    if (playwright != null) playwright.close();
  }

  @BeforeEach
  void beforeEach() {
    context = browser.newContext(new Browser.NewContextOptions().setBaseURL(baseUrl));

    // Ensure tests are isolated (no leftover auth/session across tests).
    context.addInitScript("() => { localStorage.clear(); }");

    if (TestConfig.trace()) {
      context.tracing().start(new Tracing.StartOptions()
        .setScreenshots(true)
        .setSnapshots(true)
        .setSources(true));
      tracingStarted = true;
    } else {
      tracingStarted = false;
    }

    page = context.newPage();
    page.setDefaultTimeout(15_000);
    page.setDefaultNavigationTimeout(30_000);
  }

  @AfterEach
  void afterEach(TestInfo testInfo) {
    Path dir = null;
    if (outcome.failed()) {
      dir = artifactDir(testInfo);
      try {
        Files.createDirectories(dir);
      } catch (Exception ignored) {
        // best-effort
      }

      try {
        if (page != null) {
          page.screenshot(new Page.ScreenshotOptions()
            .setFullPage(true)
            .setPath(dir.resolve("screenshot.png")));
          Files.writeString(dir.resolve("url.txt"), page.url() + "\n", StandardCharsets.UTF_8);
        }
      } catch (Exception ignored) {
        // best-effort
      }
    }

    if (tracingStarted) {
      try {
        Path tracePath = outcome.failed() && dir != null ? dir.resolve("trace.zip") : null;
        if (tracePath != null) {
          Files.createDirectories(tracePath.getParent());
          context.tracing().stop(new Tracing.StopOptions().setPath(tracePath));
        } else {
          context.tracing().stop();
        }
      } catch (Exception ignored) {
        // best-effort
      }
    }

    if (context != null) context.close();
  }

  protected void loginSeed() {
    page.navigate("/auth/login");
    page.locator(".field:has(label:has-text('Email')) input").fill(TestConfig.seedEmail());
    page.locator(".field:has(label:has-text('Mật khẩu')) input[type='password']").fill(TestConfig.seedPassword());
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Đăng nhập")).click();
    page.waitForURL("**/discover");
  }

  private static Path artifactDir(TestInfo testInfo) {
    String cls = testInfo.getTestClass().map(Class::getSimpleName).orElse("UnknownClass");
    String method = testInfo.getTestMethod().map(m -> m.getName()).orElse("unknownTest");
    String ts = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now());
    String name = sanitize(cls + "." + method + "-" + ts);
    return TestConfig.artifactsDir().resolve(name);
  }

  private static String sanitize(String s) {
    // Keep it filesystem-friendly across macOS/Linux/Windows.
    String cleaned = s.replaceAll("[^a-zA-Z0-9._-]+", "_");
    return cleaned.length() > 140 ? cleaned.substring(0, 140) : cleaned;
  }
}
