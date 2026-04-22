package com.nearmatch.framework.ui.pom.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Minimal POM base: keep selectors and actions inside page objects,
 * keep assertions in tests.
 */
public abstract class BasePage {
  protected final Page page;

  protected BasePage(Page page) {
    this.page = page;
  }

  public Page raw() {
    return page;
  }

  public String url() {
    return page.url();
  }

  protected void open(String path) {
    page.navigate(path);
  }

  protected Locator fieldInputByLabel(String labelText) {
    return page.locator(".field:has(label:has-text('" + labelText + "')) input");
  }

  protected Locator fieldPasswordByLabel(String labelText) {
    return page.locator(".field:has(label:has-text('" + labelText + "')) input[type='password']");
  }

  protected Locator fieldSelectByLabel(String labelText) {
    return page.locator(".field:has(label:has-text('" + labelText + "')) select");
  }

  protected Locator fieldTextareaByLabel(String labelText) {
    return page.locator(".field:has(label:has-text('" + labelText + "')) textarea");
  }
}

