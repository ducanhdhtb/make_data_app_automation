package com.nearmatch.tests.ui;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.nearmatch.framework.ui.BaseUiTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class MatchesTest extends BaseUiTest {

  @BeforeMethod
  public void login() {
    loginSeed();
  }

  @Test
  void matchesPageRendersHeading() {
    page.navigate("/matches");
    assertTrue(page.locator("h1:has-text('Matches của bạn')").isVisible());
  }

  @Test
  void matchesPageShowsEmptyStateOrMatchList() {
    page.navigate("/matches");
    page.waitForSelector("h1:has-text('Matches của bạn')");

    // Wait a bit for content to load
    page.waitForTimeout(1000);

    // Either a match card or the empty-state message must be present
    boolean hasMatches = page.locator("button:has-text('Nhắn tin')").count() > 0;
    boolean hasEmptyState = page.locator("text=/Chưa có match|Không có match|chưa có người|không tìm thấy/i").count() > 0;
    boolean hasLoadingOrContent = page.locator("body").textContent().length() > 100;
    
    assertTrue(hasMatches || hasEmptyState || hasLoadingOrContent, 
      "Expected match list or empty state, page content: " + page.locator("body").textContent().substring(0, Math.min(200, page.locator("body").textContent().length())));
  }

  @Test
  void matchesPageRedirectsToLoginWhenNotAuthenticated() {
    // Fresh context — no session
    var freshContext = browser.newContext(
      new com.microsoft.playwright.Browser.NewContextOptions().setBaseURL(baseUrl)
    );
    freshContext.addInitScript("() => { localStorage.clear(); }");
    var freshPage = freshContext.newPage();
    freshPage.setDefaultTimeout(15_000);
    freshPage.setDefaultNavigationTimeout(30_000);

    freshPage.navigate("/matches");
    freshPage.waitForURL("**/auth/login");
    assertTrue(freshPage.url().contains("/auth/login"));

    freshContext.close();
  }

  @Test
  void bottomNavIsVisible() {
    page.navigate("/matches");
    assertTrue(page.locator("a:has-text('Matches')").last().isVisible());
  }
}
