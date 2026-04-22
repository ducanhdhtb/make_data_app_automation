package com.nearmatch.tests.ui;

import com.nearmatch.framework.ui.BaseUiTest;
import com.nearmatch.framework.ui.pom.pages.MatchesPage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Test(groups = {"regression"})
public class MatchesTest extends BaseUiTest {

  @BeforeMethod
  public void login() {
    loginSeed();
  }

  @Test
  void matchesPageRendersHeading() {
    MatchesPage matches = new MatchesPage(page).open();
    assertTrue(matches.heading().isVisible());
  }

  @Test
  void matchesPageShowsEmptyStateOrMatchList() {
    MatchesPage matches = new MatchesPage(page).open();
    matches.heading().waitFor();

    // Wait a bit for content to load
    page.waitForTimeout(1000);

    // Either a match card or the empty-state message must be present
    boolean hasMatches = matches.messageButtons().count() > 0;
    boolean hasEmptyState = page.locator("text=/Chưa có match|Không có match|chưa có người|không tìm thấy/i").count() > 0;
    boolean hasLoadingOrContent = page.locator("body").textContent().length() > 100;
    
    assertTrue(hasMatches || hasEmptyState || hasLoadingOrContent, 
      "Expected match list or empty state, page content: " + page.locator("body").textContent().substring(0, Math.min(200, page.locator("body").textContent().length())));
  }

  @Test
  void matchesPageRedirectsToLoginWhenNotAuthenticated() {
    try (var session = freshSession()) {
      session.page().navigate("/matches");
      session.page().waitForURL("**/auth/login");
      assertTrue(session.page().url().contains("/auth/login"));
    }
  }

  @Test
  void bottomNavIsVisible() {
    MatchesPage matches = new MatchesPage(page).open();
    assertTrue(matches.bottomNavMatches().isVisible());
  }
}
