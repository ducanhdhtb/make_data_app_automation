package com.nearmatch.framework.ui.pom.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public final class MatchesPage extends BasePage {
  public MatchesPage(Page page) {
    super(page);
  }

  public MatchesPage open() {
    open("/matches");
    return this;
  }

  public Locator heading() {
    return page.locator("h1:has-text('Matches của bạn')");
  }

  public Locator messageButtons() {
    return page.locator("button:has-text('Nhắn tin')");
  }

  public Locator bottomNavMatches() {
    return page.locator("a:has-text('Matches')").last();
  }
}

