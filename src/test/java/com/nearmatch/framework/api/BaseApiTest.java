package com.nearmatch.framework.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.nearmatch.framework.config.TestConfig;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class for API-level tests.
 *
 * Playwright's APIRequestContext resolves paths like a browser:
 *   baseURL = "http://host/api"  +  path "/foo"  →  "http://host/foo"  (WRONG)
 *   baseURL = "http://host/api/" +  path "foo"   →  "http://host/api/foo" (OK)
 *
 * So we set baseURL to "http://localhost:3001/api/" (trailing slash) and
 * every helper strips the leading slash from the path before calling.
 *
 * Default: http://localhost:3001/api/  (override with -DapiUrl=http://host/api/)
 */
public abstract class BaseApiTest {

  protected static String apiUrl;
  private static Playwright playwright;
  protected static final ObjectMapper MAPPER = new ObjectMapper();
  private static final AtomicInteger CLASS_REFCOUNT = new AtomicInteger(0);

  protected APIRequestContext request;

  protected static String SEED_EMAIL;
  protected static String SEED_PASSWORD;

  @BeforeClass(alwaysRun = true)
  public void beforeClass() {
    CLASS_REFCOUNT.incrementAndGet();
    ensureStarted();
  }

  @AfterClass(alwaysRun = true)
  public void afterClass() {
    if (CLASS_REFCOUNT.decrementAndGet() == 0) {
      if (playwright != null) playwright.close();
      playwright = null;
    }
  }

  @BeforeMethod(alwaysRun = true)
  public void beforeMethod() {
    request = newContext(null);
  }

  @AfterMethod(alwaysRun = true)
  public void afterMethod() {
    if (request != null) request.dispose();
  }

  // ── helpers ───────────────────────────────────────────────────────────────

  /** Strip leading slash so paths resolve relative to baseURL. */
  protected static String p(String path) {
    return path.startsWith("/") ? path.substring(1) : path;
  }

  /** Parse response body as a JsonNode. */
  protected JsonNode parseJson(APIResponse res) {
    try {
      return MAPPER.readTree(res.body());
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse JSON: " + res.text(), e);
    }
  }

  /** Log in and return the Bearer token. */
  protected String loginAndGetToken(String email, String password) {
    APIResponse response = request.post(p("/auth/login"),
      com.microsoft.playwright.options.RequestOptions.create()
        .setData(Map.of("email", email, "password", password)));
    if (!response.ok()) {
      throw new RuntimeException("Login failed: " + response.status() + " " + response.text());
    }
    return parseJson(response).get("accessToken").asText();
  }

  /** Return a new APIRequestContext with Authorization header pre-set. */
  protected APIRequestContext authedRequest(String token) {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    headers.put("Authorization", "Bearer " + token);
    return newContext(headers);
  }

  private APIRequestContext newContext(Map<String, String> extraHeaders) {
    Map<String, String> headers = new HashMap<>();
    headers.put("Content-Type", "application/json");
    if (extraHeaders != null) headers.putAll(extraHeaders);
    return playwright.request().newContext(
      new APIRequest.NewContextOptions()
        .setBaseURL(apiUrl)
        .setExtraHTTPHeaders(headers)
    );
  }

  private static synchronized void ensureStarted() {
    if (playwright != null && apiUrl != null && SEED_EMAIL != null && SEED_PASSWORD != null) return;
    apiUrl = TestConfig.apiUrl();
    SEED_EMAIL = TestConfig.seedEmail();
    SEED_PASSWORD = TestConfig.seedPassword();
    playwright = Playwright.create();
  }

}
