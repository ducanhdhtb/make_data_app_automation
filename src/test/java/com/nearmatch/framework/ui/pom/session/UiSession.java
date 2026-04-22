package com.nearmatch.framework.ui.pom.session;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;

/**
 * Helper for "fresh" (unauthenticated) sessions in redirect tests.
 */
public final class UiSession implements AutoCloseable {
  private final BrowserContext context;
  private final Page page;

  public UiSession(Browser browser, String baseUrl) {
    this.context = browser.newContext(new Browser.NewContextOptions().setBaseURL(baseUrl));
    this.context.addInitScript("() => { localStorage.clear(); }");
    this.page = context.newPage();
    this.page.setDefaultTimeout(15_000);
    this.page.setDefaultNavigationTimeout(30_000);
  }

  public Page page() {
    return page;
  }

  @Override
  public void close() {
    context.close();
  }
}

