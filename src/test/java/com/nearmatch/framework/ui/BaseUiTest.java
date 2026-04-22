package com.nearmatch.framework.ui;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.BrowserContext;
import com.nearmatch.framework.config.TestConfig;
import com.nearmatch.framework.ui.pom.pages.LoginPage;
import com.nearmatch.framework.ui.pom.session.UiSession;
import io.qameta.allure.Allure;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseUiTest {
  protected static String baseUrl;
  private static Playwright playwright;
  protected static Browser browser;
  private static final AtomicInteger CLASS_REFCOUNT = new AtomicInteger(0);

  protected BrowserContext context;
  protected Page page;

  private boolean tracingStarted;

  @BeforeClass(alwaysRun = true)
  public void beforeClass() {
    CLASS_REFCOUNT.incrementAndGet();
    ensureBrowserStarted();
  }

  @AfterClass(alwaysRun = true)
  public void afterClass() {
    if (CLASS_REFCOUNT.decrementAndGet() == 0) {
      if (browser != null) browser.close();
      if (playwright != null) playwright.close();
      browser = null;
      playwright = null;
    }
  }

  @BeforeMethod(alwaysRun = true)
  public void beforeMethod() {
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

  @AfterMethod(alwaysRun = true)
  public void afterMethod(ITestResult result) {
    boolean success = result == null || result.isSuccess();
    Path dir = null;

    if (!success) {
      dir = artifactDir(result);
      try {
        Files.createDirectories(dir);
      } catch (Exception ignored) {
        // best-effort
      }
    }

    if (!success && page != null) {
      try {
        byte[] png = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
        Files.write(dir.resolve("screenshot.png"), png);
        Allure.addAttachment("screenshot", "image/png", new ByteArrayInputStream(png), ".png");
      } catch (Exception ignored) {
        // best-effort
      }

      try {
        String url = page.url() + "\n";
        Files.writeString(dir.resolve("url.txt"), url, StandardCharsets.UTF_8);
        Allure.addAttachment("url", "text/plain", url);
      } catch (Exception ignored) {
        // best-effort
      }
    }

    if (tracingStarted) {
      try {
        if (success) {
          context.tracing().stop();
        } else {
          Path traceZip = dir.resolve("trace.zip");
          context.tracing().stop(new Tracing.StopOptions().setPath(traceZip));
          if (Files.exists(traceZip)) {
            Allure.addAttachment("trace", "application/zip", Files.newInputStream(traceZip), ".zip");
          }
        }
      } catch (Exception ignored) {
        // best-effort
      }
    }

    if (context != null) context.close();
  }

  protected void loginSeed() {
    new LoginPage(page)
      .open()
      .loginAs(TestConfig.seedEmail(), TestConfig.seedPassword());
  }

  protected UiSession freshSession() {
    return new UiSession(browser, baseUrl);
  }

  public final Page page() {
    return page;
  }

  public final BrowserContext context() {
    return context;
  }

  public final boolean isTracingStarted() {
    return tracingStarted;
  }

  private static Path artifactDir(ITestResult result) {
    String cls = result != null && result.getTestClass() != null
      ? result.getTestClass().getRealClass().getSimpleName()
      : "UnknownClass";
    String method = result != null && result.getMethod() != null
      ? result.getMethod().getMethodName()
      : "unknownTest";
    String ts = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").format(LocalDateTime.now());
    String name = sanitize(cls + "." + method + "-" + ts);
    return TestConfig.artifactsDir().resolve(name);
  }

  private static String sanitize(String s) {
    String cleaned = s.replaceAll("[^a-zA-Z0-9._-]+", "_");
    return cleaned.length() > 140 ? cleaned.substring(0, 140) : cleaned;
  }

  private static synchronized void ensureBrowserStarted() {
    if (browser != null && playwright != null) return;

    baseUrl = TestConfig.baseUrl();
    boolean headless = TestConfig.headless();

    playwright = Playwright.create();
    BrowserType browserType = switch (TestConfig.browserName().toLowerCase()) {
      case "chromium" -> playwright.chromium();
      case "firefox" -> playwright.firefox();
      case "webkit" -> playwright.webkit();
      default -> throw new IllegalArgumentException("Unsupported browser: " + TestConfig.browserName());
    };
    browser = browserType.launch(new BrowserType.LaunchOptions()
      .setHeadless(headless)
      .setSlowMo(TestConfig.slowMoMs()));
  }
}
